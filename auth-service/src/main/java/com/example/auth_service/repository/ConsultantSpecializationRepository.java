package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultantSpecializationRepository extends JpaRepository<ConsultantSpecialization, UUID> {
    Optional<ConsultantSpecialization> findByName(String name);
    List<ConsultantSpecialization> findByNameContainingIgnoreCase(String namePart);
} 