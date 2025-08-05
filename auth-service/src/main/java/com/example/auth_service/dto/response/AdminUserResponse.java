package com.example.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private UUID id;
    private String email;
    private String userName;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private boolean isBlocked;
    private boolean isEnabledVerification;
} 