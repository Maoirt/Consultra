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
public class AdminConsultantResponse {
    private UUID id;
    private UUID userId;
    private String city;
    private Integer experienceYears;
    private String about;
    private String avatarUrl;
    private String profession;
    private boolean isBlocked;
} 