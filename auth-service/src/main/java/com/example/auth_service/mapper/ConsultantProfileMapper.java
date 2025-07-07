package com.example.auth_service.mapper;

import com.example.auth_service.dto.ConsultantProfileDto;
import com.example.auth_service.model.Consultant;
import com.example.auth_service.model.User;

public class ConsultantProfileMapper {
    public static ConsultantProfileDto toDto(Consultant consultant, User user) {
        return ConsultantProfileDto.builder()
                .id(consultant.getId())
                .userId(consultant.getUserId())
                .avatarUrl(consultant.getAvatarUrl())
                .about(consultant.getAbout())
                .experienceYears(consultant.getExperienceYears())
                .city(consultant.getCity())
                .profession(consultant.getProfession())
                .createdAt(consultant.getCreatedAt())
                .firstName(user != null ? user.getFirstName() : null)
                .lastName(user != null ? user.getLastName() : null)
                .build();
    }
} 