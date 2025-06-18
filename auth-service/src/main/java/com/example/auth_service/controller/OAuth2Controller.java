package com.example.auth_service.controller;

import com.example.auth_service.security.UserAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private UserAuthProvider userAuthProvider;

    @GetMapping("/login/success")
    public ResponseEntity<Map<String, String>> oauth2LoginSuccess(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized123"));
        }

        String email = oidcUser.getAttribute("email");
        if (email == null || email.trim().isEmpty()) {
            email = oidcUser.getAttribute("sub");
        }

        String token = userAuthProvider.createToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);

        return ResponseEntity.ok(response);
    }
}