package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consultant")
public class Consultant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "avatar_url", length = 256)
    private String avatarUrl;

    @Column(name = "about", length = 1000)
    private String about;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "city", length = 30)
    private String city;

    @Column(name = "profession", length = 100)
    private String profession;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 