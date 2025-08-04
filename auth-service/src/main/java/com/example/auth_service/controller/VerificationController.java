package com.example.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.example.auth_service.dto.response.VerificationCodeResponse;
import com.example.auth_service.dto.response.CodeVerificationResponse;
import com.example.auth_service.dto.request.VerificationRequest;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:80"})
@Tag(name = "VerificationController", description = "Контроллер для верификации пользователей")
public class VerificationController {

    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);
    private final RestTemplate restTemplate;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Value("${notification.service.url:http://localhost:8081}")
    private String notificationServiceUrl;

    @CrossOrigin(origins = {"http://localhost:3000", "http://frontend:80"})
    @PostMapping("/send-verification-code")
    public VerificationCodeResponse sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("POST /send-verification-code - email: {}", email);

        String code = String.format("%06d", (int) (Math.random() * 1000000));
        log.info("Generated verification code: {}", code);

        if (code == null || code.isEmpty()) {
            log.error("Failed to generate verification code for email: {}", email);
            return new VerificationCodeResponse(false, "Failed to generate verification code");
        }

        verificationCodes.put(email, code);

        Map<String, String> notificationRequest = new HashMap<>();
        notificationRequest.put("email", email);
        notificationRequest.put("code", code);

        log.info("Sending notification request: {}", notificationRequest);
        log.info("Notification service URL: {}", notificationServiceUrl + "/send-verification-code");
        log.info("Request email: {}, code: {}", notificationRequest.get("email"), notificationRequest.get("code"));

        try {
            log.info("About to send request to notification service");
            log.info("Request object: {}", notificationRequest);
            log.info("Request keys: {}", notificationRequest.keySet());
            log.info("Request values: {}", notificationRequest.values());
            log.info("Request size: {}", notificationRequest.size());
            log.info("Contains email: {}", notificationRequest.containsKey("email"));
            log.info("Contains code: {}", notificationRequest.containsKey("code"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(notificationRequest, headers);
            
            log.info("HttpEntity body: {}", entity.getBody());
            log.info("HttpEntity headers: {}", entity.getHeaders());

            Object response = restTemplate.postForObject(
                notificationServiceUrl + "/send-verification-code",
                entity,
                Map.class
            );
            log.info("Notification service response: {}", response);
            log.info("Verification code sent to {}", email);
            return new VerificationCodeResponse(true, "Verification code sent successfully");
        } catch (Exception e) {
            log.error("Failed to send verification code to {}", email, e);
            return new VerificationCodeResponse(false, "Failed to send verification code");
        }
    }

    @CrossOrigin(origins = {"http://localhost:3000", "http://frontend:80"})
    @PostMapping("/verify-code")
    public CodeVerificationResponse verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        log.info("POST /verify-code - email: {}, code: {}", email, code);

        String storedCode = verificationCodes.get(email);
        boolean verified = storedCode != null && storedCode.equals(code);

        if (verified) {
            log.info("Verification code for {} is valid", email);
            verificationCodes.remove(email);
            return new CodeVerificationResponse(true, "Code verified successfully");
        } else {
            log.warn("Verification code for {} is invalid", email);
            return new CodeVerificationResponse(false, "Invalid verification code");
        }
    }
} 