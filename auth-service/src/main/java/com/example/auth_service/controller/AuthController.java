package com.example.auth_service.controller;

import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.security.UserAuthProvider;
import com.example.auth_service.dto.CredentialsDto;
import com.example.auth_service.dto.SignUpDto;
import com.example.auth_service.dto.UserDto;
//import com.example.auth_service.service.impl.CustomOAuth2UserService;
import com.example.auth_service.service.impl.UserServiceImpl;
import com.example.auth_service.service.impl.VerificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.example.auth_service.repository.ConsultantRepository;

@RestController
@RequiredArgsConstructor
@Tag(name = "AuthController Controller", description = "Контроллер для авторизации и регистрации")
public class AuthController {

    private final UserServiceImpl userService;
    private final UserAuthProvider userAuthProvider;
    private final VerificationServiceImpl verificationService;
    private final ConsultantRepository consultantRepository;
    //private final CustomOAuth2UserService customOAuth2UserService;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    @Operation(summary = "Вход", description = "Позволяет войти в приложение")
    public ResponseEntity<UserDto> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о пользователе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CredentialsDto.class))
            )
            @RequestBody CredentialsDto credentialsDto) {

       UserDto user = userService.login(credentialsDto);
       user.setToken(userAuthProvider.createToken(user.getEmail()));

       return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:3000")
    @Operation(summary = "Вход", description = "Позволяет зарегистрироваться в приложении")
    public ResponseEntity<Map<String, Object>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о пользователе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpDto.class))
            )
            @RequestBody SignUpDto signUpDto) {

        UserDto user = userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));

        Map<String, Object> response = new HashMap<>();
        response.put("token", user.getToken());
        response.put("email", user.getEmail());
        response.put("role", signUpDto.getRole());
        response.put("id", user.getId());

        if ("CONSULTANT".equalsIgnoreCase(signUpDto.getRole())) {
            consultantRepository.findByUserId(user.getId()).ifPresent(consultant ->
                response.put("consultantId", consultant.getId())
            );
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    @CrossOrigin(origins = "http://localhost:3000")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        String result = verificationService.validateVerificationToken(token);
        User user = userService.findByVerificationToken(token);
        user.setEnabledVerification(true);
        userService.saveUser(user);
        if (result.equals("valid")) {
            model.addAttribute("message", "Your account has been verified successfully.");
            return "verified";
        } else {
            model.addAttribute("message", "Invalid verification token.");
            return "verify-email";
        }
    }


//    @GetMapping("/oauth2/callback")
//    @CrossOrigin(origins = "http://localhost:3000")
//    public ResponseEntity<UserDto> oauth2Callback(Authentication authentication) {
//        if (authentication == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        if (!(authentication instanceof OAuth2AuthenticationToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
//        OidcUser  oidcUser  = (OidcUser ) oauth2Token.getPrincipal();
//
//        String username = oidcUser .getAttribute("sub");
//        if (username == null || username.trim().isEmpty()) {
//            username = oidcUser .getAttribute("login");
//        }
//        String token = userAuthProvider.createToken(username);
//
//        UserDto userDto = new UserDto();
//        userDto.setUserName(username);
//        userDto.setToken(token);
//
//        return ResponseEntity.ok(userDto);
//    }
//
//    @GetMapping("/oauth2/callback")
//    @CrossOrigin(origins = "http://localhost:3000")
//    public ResponseEntity<UserDto> oauth2Callback(OAuth2AuthenticationToken authentication) {
//        if (authentication == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        OidcUser  oidcUser  = (OidcUser ) authentication.getPrincipal();
//        String username = oidcUser .getAttribute("sub");
//        if (username == null || username.trim().isEmpty()) {
//            username = oidcUser .getAttribute("login");
//        }
//
//        String token = userAuthProvider.createToken(username);
//
//        UserDto userDto = new UserDto();
//        userDto.setUserName(username);
//        userDto.setToken(token);
//
//        return ResponseEntity.ok(userDto);
//    }
}
