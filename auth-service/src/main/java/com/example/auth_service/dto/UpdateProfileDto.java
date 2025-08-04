package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {

    private UUID id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String token;
    private String role;

}

