package com.example.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class VerificationController {

    private final RestTemplate restTemplate;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8083";

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, Boolean>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

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
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        String storedCode = verificationCodes.get(email);
        boolean verified = storedCode != null && storedCode.equals(code);
        
        if (verified) {
            verificationCodes.remove(email);
        }
        
        return ResponseEntity.ok(Map.of("verified", verified));
    }
} 