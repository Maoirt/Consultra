package com.example.auth_service.repository;

import com.example.auth_service.model.ConsultantDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultantDocumentsRepository extends JpaRepository<ConsultantDocuments, UUID> {
    List<ConsultantDocuments> findByConsultantId(UUID consultantId);
} 