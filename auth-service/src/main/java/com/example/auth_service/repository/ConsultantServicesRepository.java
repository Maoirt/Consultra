package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultantServicesRepository extends JpaRepository<ConsultantServices, UUID> {
    List<ConsultantServices> findByConsultantId(UUID consultantId);
} 