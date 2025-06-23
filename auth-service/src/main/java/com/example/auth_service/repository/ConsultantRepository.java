package com.example.auth_service.repository;

import com.example.auth_service.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {
    Optional<Consultant> findByUserId(UUID userId);
} 