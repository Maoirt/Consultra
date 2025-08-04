package com.example.auth_service.controller;

import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import com.example.auth_service.security.UserAuthProvider;
import com.example.auth_service.service.impl.UserServiceImpl;
import com.example.auth_service.dto.response.OAuth2LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2Controller", description = "Контроллер для OAuth2 аутентификации")
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private UserAuthProvider userAuthProvider;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/login/success")
    public OAuth2LoginResponse oauth2LoginSuccess(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            log.warn("OAuth2 login failed: oidcUser is null");
            return new OAuth2LoginResponse(null, null, "Unauthorized", false);
        }

        String email = oidcUser.getAttribute("email");
        if (email == null || email.trim().isEmpty()) {
            email = oidcUser.getAttribute("sub");
        }

        log.info("OAuth2 login success for email: {}", email);
        UserDto user = userService.findByEmail(email);
        String token = userAuthProvider.createToken(email, User.UserRole.valueOf(user.getRole()));

        return new OAuth2LoginResponse(token, email, "OAuth2 login successful", true);
    }
}