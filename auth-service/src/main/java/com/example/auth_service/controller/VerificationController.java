package com.example.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "VerificationController", description = "Контроллер для верификации пользователей")
public class VerificationController {

    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);
    private final RestTemplate restTemplate;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8083";

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, Boolean>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("POST /send-verification-code - email: {}", email);

        String code = String.format("%06d", (int) (Math.random() * 1000000));

        verificationCodes.put(email, code);

        Map<String, String> notificationRequest = Map.of(
            "email", email,
            "code", code
        );
        
        try {
            restTemplate.postForObject(
                NOTIFICATION_SERVICE_URL + "/send-verification-code",
                notificationRequest,
                Map.class
            );
            log.info("Verification code sent to {}", email);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("Failed to send verification code to {}", email, e);
            return ResponseEntity.ok(Map.of("success", false));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        log.info("POST /verify-code - email: {}, code: {}", email, code);
        
        String storedCode = verificationCodes.get(email);
        boolean verified = storedCode != null && storedCode.equals(code);
        
        if (verified) {
            log.info("Verification code for {} is valid", email);
            verificationCodes.remove(email);
        } else {
            log.warn("Verification code for {} is invalid", email);
        }
        
        return ResponseEntity.ok(Map.of("verified", verified));
    }
} 