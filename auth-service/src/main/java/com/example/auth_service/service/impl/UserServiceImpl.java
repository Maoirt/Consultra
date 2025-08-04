package com.example.auth_service.service.impl;

import com.example.auth_service.dto.*;
import com.example.auth_service.exception.UserException;
import com.example.auth_service.mapper.ProfileMapper;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.Consultant;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.request.EmailRequest;
import com.example.auth_service.service.ConsultantService;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.ActivationTokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper profileMapper;
    private final ConsultantService consultantService;

    @Autowired
    private RestTemplate restTemplate;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public User findByVerificationToken(String token){
        User user = userRepository.findByVerificationToken(token).orElseThrow(()->new UserException("Unknown user with token", HttpStatus.NOT_FOUND));
        return user;
    }

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail()).orElseThrow(()->new UserException("Unknown user", HttpStatus.NOT_FOUND));

        if(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())){
            return userMapper.toUserDto(user);
        }

        throw new UserException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if(optionalUser.isPresent()){
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        userRepository.save(user);

        if ("CONSULTANT".equalsIgnoreCase(userDto.getRole())) {
            Consultant consultant = Consultant.builder()
                .userId(user.getId())
                .createdAt(java.time.LocalDateTime.now())
                .build();
            consultantService.registerConsultant(user.getId(), consultant, List.of());
        }

        String token = ActivationTokenGenerator.generateToken();
        user.setVerificationToken(token);
        userRepository.save(user);



        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UpdateProfileDto updateProfile(String email, UpdateProfileDto userDto) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));


        if (userDto.getFirstName() != null) {
            existingUser.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            existingUser.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getPhone() != null) {
            existingUser.setPhone(userDto.getPhone());
        }

        User updatedUser = userRepository.save(existingUser);
        return profileMapper.toUpdateProfileDto(updatedUser);
    }

    @Override
    public UpdateProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        return profileMapper.toUpdateProfileDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void setUserBlocked(UUID id, boolean blocked) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        user.setBlocked(blocked);
        userRepository.save(user);
    }

    @Override
    public void changeUserRole(UUID id, String role) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        user.setRole(User.UserRole.valueOf(role.toUpperCase()));
        userRepository.save(user);
    }
    
    @Override
    public boolean forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        String resetToken = ActivationTokenGenerator.generateToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiration(java.time.LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        

        String resetUrl = System.getenv("FRONTEND_URL") != null ? System.getenv("FRONTEND_URL") : "http://localhost:3000";
        resetUrl += "/reset-password?token=" + resetToken;
        String subject = "Сброс пароля";
        String body = String.format(
            "Здравствуйте, %s!\n\n" +
            "Вы запросили сброс пароля для вашего аккаунта.\n\n" +
            "Для сброса пароля перейдите по ссылке:\n%s\n\n" +
            "Ссылка действительна в течение 24 часов.\n\n" +
            "Если вы не запрашивали сброс пароля, проигнорируйте это письмо.\n\n" +
            "С уважением,\nКоманда Consultra",
            user.getFirstName() != null ? user.getFirstName() : "пользователь",
            resetUrl
        );
        
                    try {
                EmailRequest emailRequest = new EmailRequest(email, subject, body);
                String notificationUrl = System.getenv("NOTIFICATION_SERVICE_URL") != null ? System.getenv("NOTIFICATION_SERVICE_URL") : "http://localhost:8081";
                restTemplate.postForObject(notificationUrl + "/api/email/send-email", emailRequest, Void.class);
                return true;
            } catch (Exception e) {
                log.error("Error sending reset password email to {}", email, e);
                return false;
            }
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        

        if (user.getResetTokenExpiration() == null || 
            user.getResetTokenExpiration().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
        
        return true;
    }
}
