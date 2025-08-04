package com.example.auth_service.controller;


import com.example.auth_service.dto.UpdateProfileDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.dto.request.UserProfileRequest;
import com.example.auth_service.dto.response.UserProfileResponse;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public UserProfileResponse getUserProfile(Authentication authentication) {
        String currentEmail;
        if (authentication.getPrincipal() instanceof UserDto) {
            currentEmail = ((UserDto) authentication.getPrincipal()).getEmail();
        } else {
            currentEmail = authentication.getName();
        }
        log.info("GET /api/users/profile - email: {}", currentEmail);
        
        try {
            UpdateProfileDto userProfile = userService.getUserProfile(currentEmail);
            log.debug("Retrieved user profile: {}", userProfile);
            
            UserProfileResponse response = new UserProfileResponse();
            response.setId(userProfile.getId());
            response.setEmail(userProfile.getEmail());
            response.setUserName(userProfile.getUserName());
            response.setFirstName(userProfile.getFirstName());
            response.setLastName(userProfile.getLastName());
            response.setPhone(userProfile.getPhone());
            response.setRole(userProfile.getRole());
            
            log.debug("Returning user profile response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error getting user profile for email {}: {}", currentEmail, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(@RequestBody UserProfileRequest userRequest, Authentication authentication) {
        log.info("PUT /api/users/profile - email: {}", authentication.getName());
        log.debug("Update data: {}", userRequest);
        
        try {
            // Extract email from the authenticated principal (UserDto object)
            String currentEmail;
            if (authentication.getPrincipal() instanceof UserDto) {
                currentEmail = ((UserDto) authentication.getPrincipal()).getEmail();
            } else {
                currentEmail = authentication.getName();
            }
            
            UpdateProfileDto userDto = new UpdateProfileDto();
            userDto.setUserName(userRequest.getUserName());
            userDto.setFirstName(userRequest.getFirstName());
            userDto.setLastName(userRequest.getLastName());
            userDto.setPhone(userRequest.getPhone());
            
            log.debug("Updating profile for user: {}", currentEmail);
            UpdateProfileDto updatedUser = userService.updateProfile(currentEmail, userDto);
            log.info("Profile updated successfully for user: {}", currentEmail);
            
            UserProfileResponse response = new UserProfileResponse();
            response.setId(updatedUser.getId());
            response.setEmail(updatedUser.getEmail());
            response.setUserName(updatedUser.getUserName());
            response.setFirstName(updatedUser.getFirstName());
            response.setLastName(updatedUser.getLastName());
            response.setPhone(updatedUser.getPhone());
            response.setRole(updatedUser.getRole());
            
            log.debug("Returning updated profile response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", authentication.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getUserById(@PathVariable UUID userId) {
        log.info("GET /api/users/{}", userId);
        
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            
            log.debug("Found user: {}", user.getEmail());
            
            UserProfileResponse response = new UserProfileResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setUserName(user.getFirstName() + " " + user.getLastName()); // Используем firstName + lastName
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setPhone(user.getPhone());
            response.setRole(user.getRole().name()); // Преобразуем enum в String
            
            log.debug("Returning user response: {}", response);
            return response;
        } catch (ResponseStatusException e) {
            log.warn("User not found: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Error getting user by id {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}