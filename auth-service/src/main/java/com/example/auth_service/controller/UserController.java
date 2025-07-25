package com.example.auth_service.controller;


import com.example.auth_service.dto.UpdateProfileDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "Контроллер для управления пользователями")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UpdateProfileDto> getUserProfile(Authentication authentication) {
        String currentEmail;
        if (authentication.getPrincipal() instanceof UserDto) {
            currentEmail = ((UserDto) authentication.getPrincipal()).getEmail();
        } else {
            currentEmail = authentication.getName();
        }
        log.info("GET /api/users/profile - email: {}", currentEmail);
        UpdateProfileDto userProfile = userService.getUserProfile(currentEmail);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UpdateProfileDto> updateProfile(@RequestBody UpdateProfileDto userDto, Authentication authentication) {
        log.info("PUT /api/users/profile - email: {}", authentication.getName());
        log.debug("Update data: {}", userDto);
        
        // Extract email from the authenticated principal (UserDto object)
        String currentEmail;
        if (authentication.getPrincipal() instanceof UserDto) {
            currentEmail = ((UserDto) authentication.getPrincipal()).getEmail();
        } else {
            currentEmail = authentication.getName();
        }
        
        UpdateProfileDto updatedUser = userService.updateProfile(currentEmail, userDto);
        return ResponseEntity.ok(updatedUser);
    }
}