package com.example.auth_service.controller;

import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import com.example.auth_service.service.impl.SecurityServiceImpl;
import com.example.auth_service.service.impl.UserServiceImpl;
import com.example.auth_service.dto.response.SecurityActionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequiredArgsConstructor
@Tag(name = "SecurityController", description = "Контроллер для сброса пароля и безопасности")
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);
    private final SecurityServiceImpl securityService;


    @GetMapping("/send-reset-link")
    @CrossOrigin(origins = {"http://localhost:3000", "http://frontend:80"})
    public void sendResetLink(@RequestParam("email") String email) {
        try {

            String decodedEmail = URLDecoder.decode(email, "UTF-8");
            log.info("GET /send-reset-link - email: {}", decodedEmail);
            securityService.sendResetLink(decodedEmail);


        } catch (UnsupportedEncodingException e) {
            log.error("Error decoding email: {}", email, e);
        }
    }

    @PostMapping("/security-reset-password")
    @CrossOrigin(origins = {"http://localhost:3000", "http://frontend:80"})
    public SecurityActionResponse resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            log.info("POST /security-reset-password - token: {}", token);
            securityService.resetPassword(token, newPassword);
            return new SecurityActionResponse("Пароль успешно сброшен.", true, "RESET_PASSWORD");
        } catch (IllegalArgumentException e) {
            log.warn("Failed to reset password for token: {} - {}", token, e.getMessage());
            return new SecurityActionResponse(e.getMessage(), false, "RESET_PASSWORD");
        }
    }
}
