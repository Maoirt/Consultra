package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsultantStatsRepository extends JpaRepository<ConsultantStats, UUID> {
    Optional<ConsultantStats> findByConsultantId(UUID consultantId);
} 