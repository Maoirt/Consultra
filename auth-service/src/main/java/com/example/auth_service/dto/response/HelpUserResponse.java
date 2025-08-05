package com.example.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpUserResponse {
    private UUID id;
    private String email;
    private String userName;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
} 