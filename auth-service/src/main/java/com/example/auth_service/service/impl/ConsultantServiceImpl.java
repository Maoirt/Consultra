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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultantServiceImpl implements ConsultantService {
    private static final Logger log = LoggerFactory.getLogger(ConsultantServiceImpl.class);
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
        log.info("Registering consultant for user {} with {} specializations", userId, specializationIds.size());
        log.debug("Consultant data: city={}, profession={}, experienceYears={}", 
                consultant.getCity(), consultant.getProfession(), consultant.getExperienceYears());
        
        try {
            consultant.setUserId(userId);
            Consultant saved = consultantRepository.save(consultant);
            log.info("Consultant registered successfully with id: {}", saved.getId());
            

            
            return saved;
        } catch (Exception e) {
            log.error("Error registering consultant for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Consultant getConsultant(UUID consultantId) {
        log.debug("Getting consultant by id: {}", consultantId);
        
        try {
            Consultant consultant = consultantRepository.findById(consultantId).orElseThrow();
            log.debug("Found consultant: {}", consultant);
            return consultant;
        } catch (Exception e) {
            log.error("Error getting consultant {}: {}", consultantId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Consultant updateConsultant(UUID consultantId, Consultant consultant, List<UUID> specializationIds) {
        log.info("Updating consultant {} with {} specializations", consultantId, specializationIds.size());
        log.debug("Update data: city={}, profession={}, experienceYears={}", 
                consultant.getCity(), consultant.getProfession(), consultant.getExperienceYears());
        
        try {
            Consultant existing = consultantRepository.findById(consultantId).orElseThrow();
            log.debug("Found existing consultant: {}", existing);
            
            if (consultant.getAvatarUrl() != null) {
                existing.setAvatarUrl(consultant.getAvatarUrl());
            }
            existing.setAbout(consultant.getAbout());
            existing.setExperienceYears(consultant.getExperienceYears());
            existing.setCity(consultant.getCity());
            existing.setProfession(consultant.getProfession());
            
            Consultant saved = consultantRepository.save(existing);
            log.info("Consultant updated successfully: {}", saved.getId());
            

            consultantToSpecializationRepository.deleteAll(consultantToSpecializationRepository.findByConsultantId(consultantId));
            log.debug("Deleted existing specializations for consultant: {}", consultantId);
            
            for (UUID specId : specializationIds) {
                consultantToSpecializationRepository.save(
                    ConsultantToSpecialization.builder()
                        .consultantId(consultantId)
                        .specializationId(specId)
                        .build()
                );
            }
            log.debug("Added {} specializations for consultant: {}", specializationIds.size(), consultantId);
            
            return saved;
        } catch (Exception e) {
            log.error("Error updating consultant {}: {}", consultantId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Consultant updateAvatar(UUID consultantId, String avatarUrl) {
        log.info("Updating avatar for consultant: {} with URL: {}", consultantId, avatarUrl);
        
        try {
            Consultant existing = consultantRepository.findById(consultantId).orElseThrow();
            log.debug("Found consultant: {}", existing);
            
            existing.setAvatarUrl(avatarUrl);
            Consultant saved = consultantRepository.save(existing);
            log.info("Avatar updated successfully for consultant: {}", saved.getId());
            log.debug("New avatar URL: {}", saved.getAvatarUrl());
            
            return saved;
        } catch (Exception e) {
            log.error("Error updating avatar for consultant {}: {}", consultantId, e.getMessage(), e);
            throw e;
        }
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
        log.info("Searching consultants with filters: profession={}, specializationId={}, minPrice={}, maxPrice={}", 
                profession, specializationId, minPrice, maxPrice);
        
        try {
            List<Consultant> consultants = consultantRepository.searchConsultants(profession, specializationId, minPrice, maxPrice);
            log.info("Found {} consultants matching search criteria", consultants.size());
            
            List<ConsultantProfileDto> result = consultants.stream()
                    .map(c -> {
                        User user = userRepository.findById(c.getUserId()).orElse(null);
                        ConsultantProfileDto dto = ConsultantProfileMapper.toDto(c, user);

                        List<ConsultantServices> services = consultantServicesRepository.findByConsultantId(c.getId());
                        if (!services.isEmpty()) {
                            Integer minServicePrice = services.stream()
                                    .mapToInt(ConsultantServices::getPrice)
                                    .min()
                                    .orElse(0);
                            dto.setMinPrice(minServicePrice);
                        }
                        return dto;
                    })
                    .toList();
            
            log.debug("Returning {} consultant profile DTOs", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error searching consultants: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<String> findAllProfessions() {
        return consultantRepository.findAllProfessions();
    }
} 