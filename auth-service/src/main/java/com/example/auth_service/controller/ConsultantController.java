package com.example.auth_service.controller;

import com.example.auth_service.model.*;
import com.example.auth_service.service.ConsultantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultant")
@RequiredArgsConstructor
public class ConsultantController {
    private final ConsultantService consultantService;

    @PostMapping("/register")
    public ResponseEntity<?> registerConsultant(@RequestParam UUID userId, @RequestBody Consultant consultant, @RequestParam List<UUID> specializationIds) {
        Consultant saved = consultantService.registerConsultant(userId, consultant, specializationIds);
        return ResponseEntity.created(URI.create("/consultant/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultant> getConsultant(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getConsultant(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultant> updateConsultant(@PathVariable UUID id, @RequestBody Consultant consultant, @RequestParam List<UUID> specializationIds) {
        return ResponseEntity.ok(consultantService.updateConsultant(id, consultant, specializationIds));
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

    @PostMapping("/{id}/documents")
    public ResponseEntity<ConsultantDocuments> addDocument(@PathVariable UUID id, @RequestBody ConsultantDocuments document) {
        return ResponseEntity.ok(consultantService.addDocument(id, document));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ConsultantReviews>> getReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getReviews(id));
    }
} 