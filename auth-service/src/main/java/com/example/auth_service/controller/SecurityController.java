package com.example.auth_service.controller;

import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import com.example.auth_service.service.impl.SecurityServiceImpl;
import com.example.auth_service.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityServiceImpl securityService;


    @GetMapping("/send-reset-link")
    @CrossOrigin(origins = "http://localhost:3000")
    public void sendResetLink(@RequestParam("email") String email) {
        try {

            String decodedEmail = URLDecoder.decode(email, "UTF-8");
            securityService.sendResetLink(decodedEmail);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }

    @PostMapping("/reset-password")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            securityService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Пароль успешно сброшен.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
