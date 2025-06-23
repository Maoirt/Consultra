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
@Table(name = "consultant_reviews")
public class ConsultantReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "consultant_id", nullable = false)
    private UUID consultantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "text", nullable = false, length = 150)
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 