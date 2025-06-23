package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantToSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultantToSpecializationRepository extends JpaRepository<ConsultantToSpecialization, ConsultantToSpecialization.PK> {
    List<ConsultantToSpecialization> findByConsultantId(UUID consultantId);
} 