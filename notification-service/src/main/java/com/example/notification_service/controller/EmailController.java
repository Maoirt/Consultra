package com.example.notification_service.controller;

//import com.example.notification_service.dto.request.EmailRequest;
//import com.example.notification_service.service.EmailService;

import com.example.notification_service.dto.request.EmailRequest;
import com.example.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/email")
@RequiredArgsConstructor
@Tag(name = "EmailController", description = "Контроллер для отправки email сообщений")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    @PostMapping("/send-email")
    @Operation(summary = "Отправить email", description = "Отправляет email на указанный адрес")
    public void sendEmail(@RequestBody EmailRequest emailRequest) {
        log.info("POST /api/email/send-email - email: {}", emailRequest.getEmail());
        emailService.sendEmail(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getBody());
    }
}
