package com.example.notification_service.controller;

import com.example.notification_service.dto.request.EmailRequest;
import com.example.notification_service.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "PasswordController", description = "Контроллер для сброса пароля пользователя")
public class PasswordController {
    private final EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);

    @PostMapping("/reset-password")
    @Operation(summary = "Сбросить пароль", description = "Отправляет email для сброса пароля пользователя")
    public void resetPassword(@RequestBody EmailRequest emailRequest) {
        log.info("POST /api/reset-password - email: {}", emailRequest.getEmail());
        emailService.sendEmail(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getBody());
    }
}
