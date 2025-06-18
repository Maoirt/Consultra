package com.example.auth_service.service.impl;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl {

    private final UserRepository userRepository;

    public void createVerificationToken(User user, String token) {

        user.setVerificationToken(token);
        userRepository.save(user);
    }

    public String validateVerificationToken(String token) {

        User user = userRepository.findByVerificationToken(token).orElse(null);
        if (user == null) {
            return "invalid";
        }

        user.setEnabledVerification(true);
        userRepository.save(user);
        return "valid";
    }
}
