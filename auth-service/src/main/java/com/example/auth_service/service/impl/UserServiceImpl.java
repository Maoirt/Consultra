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

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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

//        String confirmationUrl = "http://localhost:8081/verify-email?token=" + token;
//        EmailRequest emailRequest = new EmailRequest(user.getEmail(), "Email Verification", "Click the link to verify your email: " + confirmationUrl);
//        restTemplate.postForObject("http://localhost:8083/api/email/send-email", emailRequest, Void.class);

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UpdateProfileDto updateProfile(String email, UpdateProfileDto userDto) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        // Update only non-null fields
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
}
