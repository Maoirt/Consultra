package com.example.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantRegisterRequest {
    private UUID userId;
    private String city;
    private Integer experienceYears;
    private String about;
    private String profession;
    private List<UUID> specializationIds;
} 