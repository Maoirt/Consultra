package com.example.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantResponse {
    private UUID id;
    private UUID userId;
    private String city;
    private Integer experienceYears;
    private String about;
    private String avatarUrl;
    private String profession;
    private String firstName;
    private String lastName;
    private Integer minPrice;
    private List<ConsultantServiceResponse> services;
    private List<ConsultantDocumentResponse> documents;
    private List<ConsultantReviewResponse> reviews;
    private List<ConsultantSpecializationResponse> specializations;
} 