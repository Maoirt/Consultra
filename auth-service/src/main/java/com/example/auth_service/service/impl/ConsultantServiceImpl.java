package com.example.auth_service.service.impl;

import com.example.auth_service.model.*;
import com.example.auth_service.repository.*;
import com.example.auth_service.service.ConsultantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultantServiceImpl implements ConsultantService {
    private final ConsultantRepository consultantRepository;
    private final ConsultantToSpecializationRepository consultantToSpecializationRepository;
    private final ConsultantSpecializationRepository consultantSpecializationRepository;
    private final ConsultantServicesRepository consultantServicesRepository;
    private final ConsultantDocumentsRepository consultantDocumentsRepository;
    private final ConsultantReviewsRepository consultantReviewsRepository;

    @Override
    @Transactional
    public Consultant registerConsultant(UUID userId, Consultant consultant, List<UUID> specializationIds) {
        consultant.setUserId(userId);
        Consultant saved = consultantRepository.save(consultant);
        for (UUID specId : specializationIds) {
            consultantToSpecializationRepository.save(
                ConsultantToSpecialization.builder()
                    .consultantId(saved.getId())
                    .specializationId(specId)
                    .build()
            );
        }
        return saved;
    }

    @Override
    public Consultant getConsultant(UUID consultantId) {
        return consultantRepository.findById(consultantId).orElseThrow();
    }

    @Override
    @Transactional
    public Consultant updateConsultant(UUID consultantId, Consultant consultant, List<UUID> specializationIds) {
        Consultant existing = consultantRepository.findById(consultantId).orElseThrow();
        existing.setAvatarUrl(consultant.getAvatarUrl());
        existing.setAbout(consultant.getAbout());
        existing.setExperienceYears(consultant.getExperienceYears());
        existing.setCity(consultant.getCity());
        consultantRepository.save(existing);
        consultantToSpecializationRepository.deleteAll(consultantToSpecializationRepository.findByConsultantId(consultantId));
        for (UUID specId : specializationIds) {
            consultantToSpecializationRepository.save(
                ConsultantToSpecialization.builder()
                    .consultantId(consultantId)
                    .specializationId(specId)
                    .build()
            );
        }
        return existing;
    }

    @Override
    public List<ConsultantServices> getServices(UUID consultantId) {
        return consultantServicesRepository.findByConsultantId(consultantId);
    }

    @Override
    public ConsultantServices addService(UUID consultantId, ConsultantServices service) {
        service.setConsultantId(consultantId);
        return consultantServicesRepository.save(service);
    }

    @Override
    public List<ConsultantDocuments> getDocuments(UUID consultantId) {
        return consultantDocumentsRepository.findByConsultantId(consultantId);
    }

    @Override
    public ConsultantDocuments addDocument(UUID consultantId, ConsultantDocuments document) {
        document.setConsultantId(consultantId);
        return consultantDocumentsRepository.save(document);
    }

    @Override
    public List<ConsultantReviews> getReviews(UUID consultantId) {
        return consultantReviewsRepository.findByConsultantId(consultantId);
    }
} 