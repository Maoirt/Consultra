package com.example.notification_service.controller;

import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, Boolean>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        log.info("Received verification code request for email: {}", email);
        
        boolean success = notificationService.sendVerificationEmail(email, code);
        
        if (success) {
            log.info("Successfully sent verification code to: {}", email);
        } else {
            log.error("Failed to send verification code to: {}", email);
        }
        
        return ResponseEntity.ok(Map.of("success", success));
    }
} 