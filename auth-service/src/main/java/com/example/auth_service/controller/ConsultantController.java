package com.example.auth_service.controller;

import com.example.auth_service.model.*;
import com.example.auth_service.service.ConsultantService;
import com.example.auth_service.dto.SpecializationDto;
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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultant")
@RequiredArgsConstructor
public class ConsultantController {
    private final ConsultantService consultantService;

    @Value("${app.upload.avatar.path}")
    private String uploadDir;

    @Value("${app.upload.document.path}")
    private String uploadDirDoc;

    @PostMapping("/{id}/consultation")
    public ResponseEntity<ConsultantServices> addConsultation(@PathVariable UUID id, @RequestBody ConsultantServices service){
        return ResponseEntity.ok(consultantService.addService(id, service));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerConsultant(@RequestParam UUID userId, @RequestBody Consultant consultant, @RequestParam List<UUID> specializationIds) {
        Consultant saved = consultantService.registerConsultant(userId, consultant, specializationIds);
        return ResponseEntity.created(URI.create("/consultant/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultant> getConsultant(@PathVariable UUID id) {
        System.out.println("Getting consultant with ID: " + id);
        Consultant consultant = consultantService.getConsultant(id);
        System.out.println("Loaded consultant: " + consultant);
        System.out.println("Consultant avatarUrl: " + consultant.getAvatarUrl());
        return ResponseEntity.ok(consultant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultant> updateConsultant(@PathVariable UUID id, @RequestBody Consultant consultant, @RequestParam(required = false) List<UUID> specializationIds) {
        if (specializationIds == null) {
            specializationIds = List.of();
        }
        return ResponseEntity.ok(consultantService.updateConsultant(id, consultant, specializationIds));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<Consultant> updateProfile(@PathVariable UUID id, @RequestBody Consultant consultant) {
        Consultant existing = consultantService.getConsultant(id);
        existing.setCity(consultant.getCity());
        existing.setExperienceYears(consultant.getExperienceYears());
        existing.setAbout(consultant.getAbout());
        if (consultant.getAvatarUrl() != null) {
            existing.setAvatarUrl(consultant.getAvatarUrl());
        }
        return ResponseEntity.ok(consultantService.updateConsultant(id, existing, List.of()));
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<ConsultantServices>> getServices(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getServices(id));
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<ConsultantServices> addService(@PathVariable UUID id, @RequestBody ConsultantServices service) {
        return ResponseEntity.ok(consultantService.addService(id, service));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<ConsultantDocuments>> getDocuments(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getDocuments(id));
    }

    @PostMapping(path = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConsultantDocuments> addDocument(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String description) {
        try {

            Path uploadPath = Paths.get(uploadDirDoc);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "pdf";
            String fileName = id + "-" + System.currentTimeMillis() + "." + fileExtension;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/consultant/" + id + "/documents/" + fileName;

            ConsultantDocuments document = ConsultantDocuments.builder()
                    .consultantId(id)
                    .name(name)
                    .type(type)
                    .fileUrl(fileUrl)
                    .description(description)
                    .build();
            ConsultantDocuments saved = consultantService.addDocument(id, document);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ConsultantReviews>> getReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getReviews(id));
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileName = saveImage(id, file);
            String avatarUrl = "/consultant/" + id + "/images/" + fileName;
            Consultant updated = consultantService.updateAvatar(id, avatarUrl);
            Consultant checkConsultant = consultantService.getConsultant(id);

            return ResponseEntity.ok("Изображение загружено успешно: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке изображения");
        }
    }

    private String saveImage(UUID id, MultipartFile file) throws IOException {
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "jpg";
        
        String fileName = id + "-" + System.currentTimeMillis() + "." + fileExtension;
        
        Path filePath = uploadPath.resolve(fileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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
    public ResponseEntity<List<ConsultantSpecialization>> getSpecializations(@PathVariable UUID id) {
        List<ConsultantToSpecialization> links = consultantService.getConsultantToSpecializations(id);
        List<ConsultantSpecialization> specs = links.stream()
            .map(link -> consultantService.getSpecializationById(link.getSpecializationId()))
            .toList();
        return ResponseEntity.ok(specs);
    }

    @PostMapping("/{id}/specializations")
    public ResponseEntity<ConsultantSpecialization> addSpecialization(@PathVariable UUID id, @RequestBody SpecializationDto specializationDto) {
        ConsultantSpecialization spec = consultantService.addSpecializationToConsultant(id, specializationDto.getName());
        return ResponseEntity.ok(spec);
    }
} 