package com.example.auth_service.service.impl;

import com.example.auth_service.model.*;
import com.example.auth_service.repository.*;
import com.example.auth_service.service.ConsultantService;
import com.example.auth_service.dto.ConsultantProfileDto;
import com.example.auth_service.mapper.ConsultantProfileMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Consultant registerConsultant(UUID userId, Consultant consultant, List<UUID> specializationIds) {
        consultant.setUserId(userId);
        Consultant saved = consultantRepository.save(consultant);
//        for (UUID specId : specializationIds) {
//            consultantToSpecializationRepository.save(
//                ConsultantToSpecialization.builder()
//                    .consultantId(saved.getId())
//                    .specializationId(specId)
//                    .build()
//            );
//        }
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
        if (consultant.getAvatarUrl() != null) {
            existing.setAvatarUrl(consultant.getAvatarUrl());
        }
        existing.setAbout(consultant.getAbout());
        existing.setExperienceYears(consultant.getExperienceYears());
        existing.setCity(consultant.getCity());
        existing.setProfession(consultant.getProfession());
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
    @Transactional
    public Consultant updateAvatar(UUID consultantId, String avatarUrl) {
        System.out.println("Updating avatar for consultant: " + consultantId + " with URL: " + avatarUrl);
        Consultant existing = consultantRepository.findById(consultantId).orElseThrow();
        existing.setAvatarUrl(avatarUrl);
        Consultant saved = consultantRepository.save(existing);
        System.out.println("Saved consultant with avatarUrl: " + saved.getAvatarUrl());
        return saved;
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

    @Override
    public List<ConsultantToSpecialization> getConsultantToSpecializations(UUID consultantId) {
        return consultantToSpecializationRepository.findByConsultantId(consultantId);
    }

    @Override
    public ConsultantSpecialization getSpecializationById(UUID specializationId) {
        return consultantSpecializationRepository.findById(specializationId).orElseThrow();
    }

    @Override
    @Transactional
    public ConsultantSpecialization addSpecializationToConsultant(UUID consultantId, String specializationName) {
        ConsultantSpecialization specialization = consultantSpecializationRepository.findByName(specializationName)
            .orElseGet(() -> consultantSpecializationRepository.save(
                ConsultantSpecialization.builder().name(specializationName).build()
            ));
        // Check if already linked
        boolean alreadyLinked = consultantToSpecializationRepository.findByConsultantId(consultantId).stream()
            .anyMatch(link -> link.getSpecializationId().equals(specialization.getId()));
        if (!alreadyLinked) {
            consultantToSpecializationRepository.save(
                ConsultantToSpecialization.builder()
                    .consultantId(consultantId)
                    .specializationId(specialization.getId())
                    .build()
            );
        }
        return specialization;
    }

    @Override
    public List<ConsultantProfileDto> searchConsultants(String profession, UUID specializationId, Integer minPrice, Integer maxPrice) {
        List<Consultant> consultants = consultantRepository.searchConsultants(profession, specializationId, minPrice, maxPrice);
        return consultants.stream()
                .map(c -> {
                    User user = userRepository.findById(c.getUserId()).orElse(null);
                    return ConsultantProfileMapper.toDto(c, user);
                })
                .toList();
    }

    @Override
    public List<String> findAllProfessions() {
        return consultantRepository.findAllProfessions();
    }
} 