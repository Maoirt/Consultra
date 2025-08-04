package com.example.auth_service.controller;

import com.example.auth_service.model.*;
import com.example.auth_service.repository.ConsultantRepository;
import com.example.auth_service.service.ConsultantService;
import com.example.auth_service.dto.SpecializationDto;
import com.example.auth_service.repository.ConsultantSpecializationRepository;
import com.example.auth_service.dto.ConsultantProfileDto;
import com.example.auth_service.mapper.ConsultantProfileMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.dto.request.ConsultantRegisterRequest;
import com.example.auth_service.dto.request.ConsultantUpdateRequest;
import com.example.auth_service.dto.request.ConsultantServiceRequest;
import com.example.auth_service.dto.request.ConsultantDocumentRequest;
import com.example.auth_service.dto.request.ConsultantReviewRequest;
import com.example.auth_service.dto.request.ConsultantSearchRequest;
import com.example.auth_service.dto.response.ConsultantResponse;
import com.example.auth_service.dto.response.ConsultantServiceResponse;
import com.example.auth_service.dto.response.ConsultantDocumentResponse;
import com.example.auth_service.dto.response.ConsultantReviewResponse;
import com.example.auth_service.dto.response.ConsultantSpecializationResponse;
import com.example.auth_service.dto.response.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultant")
@RequiredArgsConstructor
@Tag(name = "ConsultantController", description = "Контроллер для управления консультантами")
public class ConsultantController {
    private static final Logger log = LoggerFactory.getLogger(ConsultantController.class);
    private final ConsultantService consultantService;
    private final ConsultantSpecializationRepository consultantSpecializationRepository;
    private final UserRepository userRepository;
    private final ConsultantRepository consultantRepository;

    @Value("${app.upload.avatar.path}")
    private String uploadDir;

    @Value("${app.upload.document.path}")
    private String uploadDirDoc;

    @PostMapping("/{id}/consultation")
    public ConsultantServiceResponse addConsultation(@PathVariable UUID id, @RequestBody ConsultantServiceRequest serviceRequest){
        log.info("POST /consultant/{}/consultation - Request: name={}, description={}, price={}", 
                id, serviceRequest.getName(), serviceRequest.getDescription(), serviceRequest.getPrice());
        
        try {
            ConsultantServices service = new ConsultantServices();
            service.setName(serviceRequest.getName());
            service.setDescription(serviceRequest.getDescription());
            service.setPrice(serviceRequest.getPrice());
            service.setConsultantId(id);
            
            log.debug("Creating service for consultant {}: {}", id, service);
            ConsultantServices savedService = consultantService.addService(id, service);
            log.info("Service created successfully for consultant {}: {}", id, savedService.getId());
            
            ConsultantServiceResponse response = ConsultantServiceResponse.builder()
                    .id(savedService.getId())
                    .name(savedService.getName())
                    .description(savedService.getDescription())
                    .price(savedService.getPrice())
                    .consultantId(savedService.getConsultantId())
                    .build();
            
            log.debug("Returning service response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error creating consultation for consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ConsultantResponse registerConsultant(@RequestBody ConsultantRegisterRequest request) {
        log.info("POST /consultant/register - userId: {}, city: {}, profession: {}, experienceYears: {}", 
                request.getUserId(), request.getCity(), request.getProfession(), request.getExperienceYears());
        
        try {
            Consultant consultant = new Consultant();
            consultant.setUserId(request.getUserId());
            consultant.setCity(request.getCity());
            consultant.setExperienceYears(request.getExperienceYears());
            consultant.setAbout(request.getAbout());
            consultant.setProfession(request.getProfession());
            
            log.debug("Registering consultant with specializations: {}", request.getSpecializationIds());
            Consultant saved = consultantService.registerConsultant(request.getUserId(), consultant, request.getSpecializationIds());
            log.info("Consultant registered successfully: {}", saved.getId());
            
            User user = userRepository.findById(saved.getUserId()).orElse(null);
            if (user == null) {
                log.warn("User not found for consultant {}: {}", saved.getId(), saved.getUserId());
            }
            
            ConsultantResponse response = ConsultantResponse.builder()
                    .id(saved.getId())
                    .userId(saved.getUserId())
                    .city(saved.getCity())
                    .experienceYears(saved.getExperienceYears())
                    .about(saved.getAbout())
                    .avatarUrl(saved.getAvatarUrl())
                    .profession(saved.getProfession())
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .build();
            
            log.debug("Returning consultant response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error registering consultant for user {}: {}", request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ConsultantResponse getConsultant(@PathVariable UUID id) {
        log.info("GET /consultant/{}", id);
        
        try {
            Consultant consultant = consultantService.getConsultant(id);
            log.debug("Found consultant: {}", consultant);
            
            User user = userRepository.findById(consultant.getUserId()).orElse(null);
            if (user == null) {
                log.warn("User not found for consultant {}: {}", id, consultant.getUserId());
            } else {
                log.debug("Found user for consultant {}: {}", id, user.getEmail());
            }
            
            ConsultantProfileDto dto = ConsultantProfileMapper.toDto(consultant, user);
            
            ConsultantResponse response = ConsultantResponse.builder()
                    .id(consultant.getId())
                    .userId(consultant.getUserId())
                    .city(consultant.getCity())
                    .experienceYears(consultant.getExperienceYears())
                    .about(consultant.getAbout())
                    .avatarUrl(consultant.getAvatarUrl())
                    .profession(consultant.getProfession())
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .build();
            
            log.debug("Returning consultant response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error getting consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ConsultantResponse updateConsultant(@PathVariable UUID id, @RequestBody ConsultantUpdateRequest request) {
        log.info("PUT /consultant/{} - city: {}, profession: {}, experienceYears: {}", 
                id, request.getCity(), request.getProfession(), request.getExperienceYears());
        
        try {
            
            Consultant consultant = new Consultant();
            consultant.setCity(request.getCity());
            consultant.setExperienceYears(request.getExperienceYears());
            consultant.setAbout(request.getAbout());
            consultant.setAvatarUrl(request.getAvatarUrl());
            consultant.setProfession(request.getProfession());
            
            List<UUID> specializationIds = request.getSpecializationIds() != null ? request.getSpecializationIds() : List.of();
            log.debug("Updating consultant {} with specializations: {}", id, specializationIds);
            
            Consultant updated = consultantService.updateConsultant(id, consultant, specializationIds);
            log.info("Consultant updated successfully: {}", updated.getId());
            
            User user = userRepository.findById(updated.getUserId()).orElse(null);
            if (user == null) {
                log.warn("User not found for updated consultant {}: {}", id, updated.getUserId());
            }
            
            ConsultantResponse response = ConsultantResponse.builder()
                    .id(updated.getId())
                    .userId(updated.getUserId())
                    .city(updated.getCity())
                    .experienceYears(updated.getExperienceYears())
                    .about(updated.getAbout())
                    .avatarUrl(updated.getAvatarUrl())
                    .profession(updated.getProfession())
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .build();
            
            log.debug("Returning updated consultant response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error updating consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/profile")
    public ConsultantResponse updateProfile(@PathVariable UUID id, @RequestBody ConsultantUpdateRequest request) {
        log.info("PUT /consultant/{}/profile", id);
        Consultant existing = consultantService.getConsultant(id);
        
        existing.setCity(request.getCity());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setAbout(request.getAbout());
        existing.setProfession(request.getProfession());
        if (request.getAvatarUrl() != null) {
            existing.setAvatarUrl(request.getAvatarUrl());
        }
        
        // Сохраняем только основные данные профиля, не трогая специализации
        existing = consultantRepository.save(existing);
        
        User user = userRepository.findById(existing.getUserId()).orElse(null);
        
        ConsultantResponse response = ConsultantResponse.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .city(existing.getCity())
                .experienceYears(existing.getExperienceYears())
                .about(existing.getAbout())
                .avatarUrl(existing.getAvatarUrl())
                .profession(existing.getProfession())
                .firstName(user != null ? user.getFirstName() : null)
                .lastName(user != null ? user.getLastName() : null)
                .build();
        
        return response;
    }

    @GetMapping("/{id}/services")
    public List<ConsultantServiceResponse> getServices(@PathVariable UUID id) {
        List<ConsultantServices> services = consultantService.getServices(id);
        return services.stream()
                .map(service -> ConsultantServiceResponse.builder()
                        .id(service.getId())
                        .name(service.getName())
                        .description(service.getDescription())
                        .price(service.getPrice())
                        .consultantId(service.getConsultantId())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/services")
    public ConsultantServiceResponse addService(@PathVariable UUID id, @RequestBody ConsultantServiceRequest serviceRequest) {
        
        ConsultantServices service = new ConsultantServices();
        service.setName(serviceRequest.getName());
        service.setDescription(serviceRequest.getDescription());
        service.setPrice(serviceRequest.getPrice());
        service.setConsultantId(id);
        
        ConsultantServices savedService = consultantService.addService(id, service);
        
        return ConsultantServiceResponse.builder()
                .id(savedService.getId())
                .name(savedService.getName())
                .description(savedService.getDescription())
                .price(savedService.getPrice())
                .consultantId(savedService.getConsultantId())
                .build();
    }

    @GetMapping("/{id}/documents")
    public List<ConsultantDocumentResponse> getDocuments(@PathVariable UUID id) {
        List<ConsultantDocuments> documents = consultantService.getDocuments(id);
        return documents.stream()
                .map(document -> ConsultantDocumentResponse.builder()
                        .id(document.getId())
                        .name(document.getName())
                        .type(document.getType())
                        .description(document.getDescription())
                        .fileUrl(document.getFileUrl())
                        .consultantId(document.getConsultantId())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConsultantDocumentResponse addDocument(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String description) {
        log.info("POST /consultant/{}/documents - name: {}, type: {}, filename: {}, size: {} bytes", 
                id, name, type, file.getOriginalFilename(), file.getSize());
        
        try {
            Path uploadPath = Paths.get(uploadDirDoc);
            if (!Files.exists(uploadPath)) {
                log.debug("Creating document upload directory: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }
            
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "pdf";
            String fileName = id + "-" + System.currentTimeMillis() + "." + fileExtension;
            Path filePath = uploadPath.resolve(fileName);
            
            log.debug("Saving document: {} -> {}", originalFilename, filePath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/consultant/" + id + "/documents/" + fileName;
            log.debug("Document URL: {}", fileUrl);

            ConsultantDocuments document = ConsultantDocuments.builder()
                    .consultantId(id)
                    .name(name)
                    .type(type)
                    .fileUrl(fileUrl)
                    .description(description)
                    .build();
            
            log.debug("Creating document record: {}", document);
            ConsultantDocuments saved = consultantService.addDocument(id, document);
            log.info("Document added successfully for consultant {}: {}", id, saved.getId());
            
            ConsultantDocumentResponse response = ConsultantDocumentResponse.builder()
                    .id(saved.getId())
                    .name(saved.getName())
                    .type(saved.getType())
                    .description(saved.getDescription())
                    .fileUrl(saved.getFileUrl())
                    .consultantId(saved.getConsultantId())
                    .build();
            
            log.debug("Returning document response: {}", response);
            return response;
        } catch (IOException e) {
            log.error("Error uploading document for consultant {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error uploading document", e);
        }
    }

    @GetMapping("/{id}/reviews")
    public List<ConsultantReviewResponse> getReviews(@PathVariable UUID id) {
        List<ConsultantReviews> reviews = consultantService.getReviews(id);
        return reviews.stream()
                .map(review -> ConsultantReviewResponse.builder()
                        .id(review.getId())
                        .comment(review.getText())
                        .rating(review.getRating())
                        .consultantId(review.getConsultantId())
                        .userId(review.getUserId())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/avatar")
    public FileUploadResponse uploadProfileImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /consultant/{}/avatar - filename: {}, size: {} bytes", 
                id, file.getOriginalFilename(), file.getSize());
        
        try {
            String fileName = saveImage(id, file);
            log.debug("Image saved successfully: {}", fileName);
            
            String avatarUrl = "/consultant/" + id + "/images/" + fileName;
            Consultant updated = consultantService.updateAvatar(id, avatarUrl);
            log.info("Avatar updated for consultant {}: {}", id, avatarUrl);
            
            Consultant checkConsultant = consultantService.getConsultant(id);

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileName(fileName)
                    .fileUrl(avatarUrl)
                    .message("Изображение загружено успешно: " + fileName)
                    .success(true)
                    .build();
            
            log.debug("Returning upload response: {}", response);
            return response;
        } catch (IOException e) {
            log.error("Error uploading avatar for consultant {}: {}", id, e.getMessage(), e);
            return FileUploadResponse.builder()
                    .message("Ошибка при загрузке изображения")
                    .success(false)
                    .build();
        }
    }

    private String saveImage(UUID id, MultipartFile file) throws IOException {
        log.debug("Saving image for consultant {}: originalFilename={}, contentType={}", 
                id, file.getOriginalFilename(), file.getContentType());
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            log.debug("Creating upload directory: {}", uploadPath);
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "jpg";
        
        log.debug("File extension: {}", fileExtension);
        
        //String fileName = id + "-" + System.currentTimeMillis() + "." + fileExtension;
        String fileName = "avatar-" + id + "-" + fileExtension;
        log.debug("Generated filename: {}", fileName);

        Path filePath = uploadPath.resolve(fileName);
        log.debug("Full file path: {}", filePath);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.debug("File saved successfully: {}", filePath);

        return fileName;
    }

    @GetMapping("/{id}/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable UUID id, @PathVariable String filename) {
        try {

            Path filePath = Paths.get(uploadDir).resolve(filename);

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg";
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (filename.toLowerCase().endsWith(".webp")) {
                    contentType = "image/webp";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/documents/{filename}")
    public ResponseEntity<Resource> getDocument(@PathVariable UUID id, @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDirDoc).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/specializations")
    public List<ConsultantSpecializationResponse> getSpecializations(@PathVariable UUID id) {
        List<ConsultantToSpecialization> links = consultantService.getConsultantToSpecializations(id);
        List<ConsultantSpecialization> specs = links.stream()
            .map(link -> consultantService.getSpecializationById(link.getSpecializationId()))
            .toList();
        return specs.stream()
                .map(spec -> ConsultantSpecializationResponse.builder()
                        .id(spec.getId())
                        .name(spec.getName())
                        .description(spec.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/specializations")
    public ConsultantSpecializationResponse addSpecialization(@PathVariable UUID id, @RequestBody SpecializationDto specializationDto) {
        ConsultantSpecialization spec = consultantService.addSpecializationToConsultant(id, specializationDto.getName());
        
        return ConsultantSpecializationResponse.builder()
                .id(spec.getId())
                .name(spec.getName())
                .description(spec.getName())
                .build();
    }

    @GetMapping("/specializations")
    public List<ConsultantSpecializationResponse> getAllSpecializations() {
        List<ConsultantSpecialization> specializations = consultantSpecializationRepository.findAll();
        return specializations.stream()
                .map(spec -> ConsultantSpecializationResponse.builder()
                        .id(spec.getId())
                        .name(spec.getName())
                        .description(spec.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/specializations/search")
    public List<ConsultantSpecializationResponse> searchSpecializations(@RequestParam("query") String query) {
        List<ConsultantSpecialization> specializations = consultantSpecializationRepository.findByNameContainingIgnoreCase(query);
        return specializations.stream()
                .map(spec -> ConsultantSpecializationResponse.builder()
                        .id(spec.getId())
                        .name(spec.getName())
                        .description(spec.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/professions")
    public List<String> getAllProfessions() {
        return consultantService.findAllProfessions();
    }

    @GetMapping("/search")
    public List<ConsultantResponse> searchConsultants(
            @RequestParam(required = false) String profession,
            @RequestParam(required = false) UUID specializationId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {
        log.info("GET /consultant/search - profession: {}, specializationId: {}, minPrice: {}, maxPrice: {}", 
                profession, specializationId, minPrice, maxPrice);
        
        try {
            List<ConsultantProfileDto> consultants = consultantService.searchConsultants(profession, specializationId, minPrice, maxPrice);
            log.info("Found {} consultants matching search criteria", consultants.size());
            
            List<ConsultantResponse> response = consultants.stream()
                    .map(dto -> ConsultantResponse.builder()
                            .id(dto.getId())
                            .userId(dto.getUserId())
                            .city(dto.getCity())
                            .experienceYears(dto.getExperienceYears())
                            .about(dto.getAbout())
                            .avatarUrl(dto.getAvatarUrl())
                            .profession(dto.getProfession())
                            .firstName(dto.getFirstName())
                            .lastName(dto.getLastName())
                            .minPrice(dto.getMinPrice())
                            .build())
                    .collect(Collectors.toList());
            
            log.debug("Returning {} consultant responses", response.size());
            return response;
        } catch (Exception e) {
            log.error("Error searching consultants: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/by-user/{userId}")
    public ConsultantResponse getConsultantByUserId(@PathVariable UUID userId) {
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = userRepository.findById(consultant.getUserId()).orElse(null);
        ConsultantProfileDto dto = ConsultantProfileMapper.toDto(consultant, user);
        
        return ConsultantResponse.builder()
                .id(consultant.getId())
                .userId(consultant.getUserId())
                .city(consultant.getCity())
                .experienceYears(consultant.getExperienceYears())
                .about(consultant.getAbout())
                .avatarUrl(consultant.getAvatarUrl())
                .profession(consultant.getProfession())
                .firstName(user != null ? user.getFirstName() : null)
                .lastName(user != null ? user.getLastName() : null)
                .build();
    }
} 