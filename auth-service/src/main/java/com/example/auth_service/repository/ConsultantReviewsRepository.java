package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantReviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultantReviewsRepository extends JpaRepository<ConsultantReviews, UUID> {
    List<ConsultantReviews> findByConsultantId(UUID consultantId);
} 