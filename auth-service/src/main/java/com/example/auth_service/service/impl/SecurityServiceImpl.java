package com.example.auth_service.service.impl;

import com.example.auth_service.dto.SignUpDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.exception.UserException;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.request.EmailRequest;
import com.example.auth_service.util.ActivationTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${notification.service.url:http://localhost:8081}")
    private String notificationServiceUrl;

    public void sendResetLink(String email){

        User user = userRepository.findByEmail(email).orElse(null);

        if(user != null){
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            String resetLink = System.getenv("FRONTEND_URL") != null ? System.getenv("FRONTEND_URL") : "http://localhost:3000";
            resetLink += "/reset-password?token=" + token;

            EmailRequest emailRequest = new EmailRequest(user.getEmail(), "Email Verification", "Click the link to reset your password: " + resetLink);
            restTemplate.postForObject(notificationServiceUrl + "/api/reset-password", emailRequest, Void.class);
        }

    }

    public void resetPassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token).orElse(null);

        if (user == null || user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Неверный или истекший токен");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }

}
