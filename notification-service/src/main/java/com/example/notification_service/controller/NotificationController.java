package com.example.notification_service.controller;

import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "NotificationController", description = "Контроллер для отправки уведомлений и кодов верификации")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    @PostMapping("/send-verification-code")
    @Operation(summary = "Отправить код верификации", description = "Отправляет код верификации на email пользователя")
    public ResponseEntity<Map<String, Boolean>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        log.info("POST /send-verification-code - email: {}, code: {}", email, code);
        
        boolean success = notificationService.sendVerificationEmail(email, code);
        
        if (success) {
            log.info("Successfully sent verification code to: {}", email);
        } else {
            log.error("Failed to send verification code to: {}", email);
        }
        
        return ResponseEntity.ok(Map.of("success", success));
    }
} 