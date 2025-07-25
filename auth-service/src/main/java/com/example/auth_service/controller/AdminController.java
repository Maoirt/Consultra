package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.service.UserService;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.model.Consultant;
import com.example.auth_service.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "AdminController", description = "Контроллер для управления администраторами и пользователями")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final UserRepository userRepository;
    private final ConsultantRepository consultantRepository;

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        log.info("GET /api/admin/users");
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/admin/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable UUID id) {
        log.info("PUT /api/admin/users/{}/block", id);
        userService.setUserBlocked(id, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID id) {
        log.info("PUT /api/admin/users/{}/unblock", id);
        userService.setUserBlocked(id, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable UUID id, @RequestParam String role) {
        log.info("PUT /api/admin/users/{}/role - new role: {}", id, role);
        userService.changeUserRole(id, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/consultants")
    public List<Consultant> getAllConsultants() {
        log.info("GET /api/admin/consultants");
        return consultantRepository.findAll();
    }

    @DeleteMapping("/consultants/{id}")
    public ResponseEntity<?> deleteConsultant(@PathVariable UUID id) {
        log.info("DELETE /api/admin/consultants/{}", id);
        consultantRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/consultants/{id}/block")
    public ResponseEntity<?> blockConsultant(@PathVariable UUID id) {
        log.info("PUT /api/admin/consultants/{}/block", id);
        Consultant consultant = consultantRepository.findById(id).orElseThrow();
        userService.setUserBlocked(consultant.getUserId(), true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/consultants/{id}/unblock")
    public ResponseEntity<?> unblockConsultant(@PathVariable UUID id) {
        log.info("PUT /api/admin/consultants/{}/unblock", id);
        Consultant consultant = consultantRepository.findById(id).orElseThrow();
        userService.setUserBlocked(consultant.getUserId(), false);
        return ResponseEntity.ok().build();
    }
} 