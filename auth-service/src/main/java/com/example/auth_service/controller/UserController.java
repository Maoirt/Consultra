package com.example.auth_service.controller;


import com.example.auth_service.dto.UpdateProfileDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UpdateProfileDto> getUserProfile(Authentication authentication) {
        String currentEmail;
        if (authentication.getPrincipal() instanceof UserDto) {
            currentEmail = ((UserDto) authentication.getPrincipal()).getEmail();
        } else {
            currentEmail = authentication.getName();
        }
        
        UpdateProfileDto userProfile = userService.getUserProfile(currentEmail);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UpdateProfileDto> updateProfile(@RequestBody UpdateProfileDto userDto, Authentication authentication) {
        System.out.println("Received update profile request for user: " + authentication.getName());
        System.out.println("Data: " + userDto);
        
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