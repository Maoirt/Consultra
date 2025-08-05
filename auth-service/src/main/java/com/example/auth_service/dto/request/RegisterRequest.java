package com.example.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private char[] password;
    private String userName;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
} 