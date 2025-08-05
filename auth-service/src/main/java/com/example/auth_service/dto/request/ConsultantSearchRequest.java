package com.example.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantSearchRequest {
    private String profession;
    private UUID specializationId;
    private Integer minPrice;
    private Integer maxPrice;
} 