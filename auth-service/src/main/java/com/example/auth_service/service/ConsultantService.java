package com.example.auth_service.service;

import com.example.auth_service.model.*;

import java.util.List;
import java.util.UUID;

public interface ConsultantService {
    Consultant registerConsultant(UUID userId, Consultant consultant, List<UUID> specializationIds);
    Consultant getConsultant(UUID consultantId);
    Consultant updateConsultant(UUID consultantId, Consultant consultant, List<UUID> specializationIds);
    Consultant updateAvatar(UUID consultantId, String avatarUrl);
    List<ConsultantServices> getServices(UUID consultantId);
    ConsultantServices addService(UUID consultantId, ConsultantServices service);
    List<ConsultantDocuments> getDocuments(UUID consultantId);
    ConsultantDocuments addDocument(UUID consultantId, ConsultantDocuments document);
    List<ConsultantReviews> getReviews(UUID consultantId);
    List<ConsultantToSpecialization> getConsultantToSpecializations(UUID consultantId);
    ConsultantSpecialization getSpecializationById(UUID specializationId);
    ConsultantSpecialization addSpecializationToConsultant(UUID consultantId, String specializationName);
    // ... другие методы по необходимости
} 