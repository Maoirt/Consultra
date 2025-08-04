package com.example.auth_service.controller;

import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.security.UserAuthProvider;
import com.example.auth_service.dto.CredentialsDto;
import com.example.auth_service.dto.SignUpDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.dto.request.LoginRequest;
import com.example.auth_service.dto.request.RegisterRequest;
import com.example.auth_service.dto.request.ForgotPasswordRequest;
import com.example.auth_service.dto.request.ResetPasswordRequest;
import com.example.auth_service.dto.response.LoginResponse;
import com.example.auth_service.dto.response.RegisterResponse;
import com.example.auth_service.dto.response.VerifyEmailResponse;
import com.example.auth_service.dto.response.ForgotPasswordResponse;
import com.example.auth_service.dto.response.ResetPasswordResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@Tag(name = "AuthController Controller", description = "Контроллер для авторизации и регистрации")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserServiceImpl userService;
    private final UserAuthProvider userAuthProvider;
    private final VerificationServiceImpl verificationService;
    private final ConsultantRepository consultantRepository;
    //private final CustomOAuth2UserService customOAuth2UserService;

    @PostMapping("/login")
    @Operation(summary = "Вход", description = "Позволяет войти в приложение")
    public LoginResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о пользователе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest loginRequest) {

       log.info("POST /login - email: {}", loginRequest.getEmail());
       
       CredentialsDto credentialsDto = new CredentialsDto();
       credentialsDto.setEmail(loginRequest.getEmail());
       credentialsDto.setPassword(loginRequest.getPassword());
       
       UserDto user = userService.login(credentialsDto);
        user.setToken(userAuthProvider.createToken(
                user.getEmail(),
                User.UserRole.valueOf(user.getRole())
        ));

       log.info("User logged in: {}", user.getEmail());
       
       LoginResponse response = new LoginResponse();
       response.setToken(user.getToken());
       response.setEmail(user.getEmail());
       response.setRole(user.getRole());
       response.setId(user.getId());
       response.setUserName(user.getUserName());
       
       return response;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация", description = "Позволяет зарегистрироваться в приложении")
    public RegisterResponse register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о пользователе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @RequestBody RegisterRequest registerRequest) {

        log.info("POST /register - email: {}", registerRequest.getEmail());
        
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setEmail(registerRequest.getEmail());
        signUpDto.setPassword(registerRequest.getPassword());
        signUpDto.setUserName(registerRequest.getUserName());
        signUpDto.setFirstName(registerRequest.getFirstName());
        signUpDto.setLastName(registerRequest.getLastName());
        signUpDto.setPhone(registerRequest.getPhone());
        signUpDto.setRole(registerRequest.getRole());
        
        UserDto user = userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(
                user.getEmail(),
                User.UserRole.valueOf(user.getRole())
        ));

        RegisterResponse response = new RegisterResponse();
        response.setToken(user.getToken());
        response.setEmail(user.getEmail());
        response.setRole(registerRequest.getRole());
        response.setId(user.getId());

        if ("CONSULTANT".equalsIgnoreCase(registerRequest.getRole())) {
            consultantRepository.findByUserId(user.getId()).ifPresent(consultant ->
                response.setConsultantId(consultant.getId())
            );
        }

        log.info("User registered: {} with role {}", user.getEmail(), registerRequest.getRole());
        return response;
    }

    @PostMapping("/verify-email")
    public VerifyEmailResponse verifyEmail(@RequestParam("token") String token) {
        log.info("POST /verify-email - token: {}", token);
        String result = verificationService.validateVerificationToken(token);
        User user = userService.findByVerificationToken(token);
        user.setEnabledVerification(true);
        userService.saveUser(user);
        
        VerifyEmailResponse response = new VerifyEmailResponse();
        if (result.equals("valid")) {
            response.setMessage("Your account has been verified successfully.");
            response.setSuccess(true);
            log.info("Email verified for user: {}", user.getEmail());
        } else {
            response.setMessage("Invalid verification token.");
            response.setSuccess(false);
            log.warn("Invalid verification token: {}", token);
        }
        return response;
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Забыл пароль", description = "Отправляет письмо для сброса пароля")
    public ForgotPasswordResponse forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForgotPasswordRequest.class))
            )
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        
        log.info("POST /forgot-password - email: {}", forgotPasswordRequest.getEmail());
        
        boolean success = userService.forgotPassword(forgotPasswordRequest.getEmail());
        
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        if (success) {
            response.setMessage("Если указанный email существует в системе, на него будет отправлено письмо для сброса пароля.");
            response.setSuccess(true);
            log.info("Password reset email sent to: {}", forgotPasswordRequest.getEmail());
        } else {
            response.setMessage("Email не найден в системе.");
            response.setSuccess(false);
            log.warn("Password reset requested for non-existent email: {}", forgotPasswordRequest.getEmail());
        }
        
        return response;
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Сброс пароля", description = "Устанавливает новый пароль по токену")
    public ResetPasswordResponse resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для сброса пароля",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequest.class))
            )
            @RequestBody ResetPasswordRequest resetPasswordRequest) {
        
        log.info("POST /reset-password - token: {}", resetPasswordRequest.getToken());
        
        boolean success = userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        
        ResetPasswordResponse response = new ResetPasswordResponse();
        if (success) {
            response.setMessage("Пароль успешно изменен.");
            response.setSuccess(true);
            log.info("Password successfully reset for token: {}", resetPasswordRequest.getToken());
        } else {
            response.setMessage("Недействительный или истекший токен для сброса пароля.");
            response.setSuccess(false);
            log.warn("Invalid or expired reset token: {}", resetPasswordRequest.getToken());
        }
        
        return response;
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
