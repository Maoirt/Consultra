package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantProfileDto {
    private UUID id;
    private UUID userId;
    private String avatarUrl;
    private String about;
    private Integer experienceYears;
    private String city;
    private String profession;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    private Integer minPrice;
} 