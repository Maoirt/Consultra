package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.service.UserService;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.model.Consultant;
import com.example.auth_service.repository.ConsultantRepository;
import com.example.auth_service.dto.response.AdminUserResponse;
import com.example.auth_service.dto.response.AdminConsultantResponse;
import com.example.auth_service.dto.response.AdminActionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
    public List<AdminUserResponse> getAllUsers() {
        log.info("GET /api/admin/users");
        
        try {
            List<UserDto> users = userService.getAllUsers();
            log.info("Retrieved {} users for admin", users.size());
            
            List<AdminUserResponse> response = users.stream()
                    .map(userDto -> AdminUserResponse.builder()
                            .id(userDto.getId())
                            .email(userDto.getEmail())
                            .userName(userDto.getUserName())
                            .firstName(userDto.getFirstName())
                            .lastName(userDto.getLastName())
                            .phone(userDto.getPhone())
                            .role(userDto.getRole())
                            .isBlocked(userDto.getIsBlocked())
                            .isEnabledVerification(userDto.getIsEnabledVerification())
                            .build())
                    .collect(Collectors.toList());
            
            log.debug("Returning {} admin user responses", response.size());
            return response;
        } catch (Exception e) {
            log.error("Error getting all users for admin: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/users/{id}")
    public AdminActionResponse deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/admin/users/{}", id);
        
        try {
            userService.deleteUser(id);
            log.info("User deleted successfully: {}", id);
            return AdminActionResponse.builder()
                    .message("User deleted successfully")
                    .success(true)
                    .action("DELETE_USER")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/users/{id}/block")
    public AdminActionResponse blockUser(@PathVariable UUID id) {
        log.info("PUT /api/admin/users/{}/block", id);
        
        try {
            userService.setUserBlocked(id, true);
            log.info("User blocked successfully: {}", id);
            return AdminActionResponse.builder()
                    .message("User blocked successfully")
                    .success(true)
                    .action("BLOCK_USER")
                    .build();
        } catch (Exception e) {
            log.error("Error blocking user {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/users/{id}/unblock")
    public AdminActionResponse unblockUser(@PathVariable UUID id) {
        log.info("PUT /api/admin/users/{}/unblock", id);
        
        try {
            userService.setUserBlocked(id, false);
            log.info("User unblocked successfully: {}", id);
            return AdminActionResponse.builder()
                    .message("User unblocked successfully")
                    .success(true)
                    .action("UNBLOCK_USER")
                    .build();
        } catch (Exception e) {
            log.error("Error unblocking user {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/users/{id}/role")
    public AdminActionResponse changeUserRole(@PathVariable UUID id, @RequestParam String role) {
        log.info("PUT /api/admin/users/{}/role - new role: {}", id, role);
        
        try {
            userService.changeUserRole(id, role);
            log.info("User role changed successfully: {} -> {}", id, role);
            return AdminActionResponse.builder()
                    .message("User role changed successfully")
                    .success(true)
                    .action("CHANGE_USER_ROLE")
                    .build();
        } catch (Exception e) {
            log.error("Error changing user role {} -> {}: {}", id, role, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/consultants")
    public List<AdminConsultantResponse> getAllConsultants() {
        log.info("GET /api/admin/consultants");
        
        try {
            List<Consultant> consultants = consultantRepository.findAll();
            log.info("Retrieved {} consultants for admin", consultants.size());
            
            List<AdminConsultantResponse> response = consultants.stream()
                    .map(consultant -> {
                        // Проверяем, заблокирован ли пользователь
                        User user = userRepository.findById(consultant.getUserId()).orElse(null);
                        return AdminConsultantResponse.builder()
                                .id(consultant.getId())
                                .userId(consultant.getUserId())
                                .city(consultant.getCity())
                                .experienceYears(consultant.getExperienceYears())
                                .about(consultant.getAbout())
                                .avatarUrl(consultant.getAvatarUrl())
                                .profession(consultant.getProfession())
                                .isBlocked(user != null && user.isBlocked())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            log.debug("Returning {} admin consultant responses", response.size());
            return response;
        } catch (Exception e) {
            log.error("Error getting all consultants for admin: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/consultants/{id}")
    public AdminActionResponse deleteConsultant(@PathVariable UUID id) {
        log.info("DELETE /api/admin/consultants/{}", id);
        
        try {
            consultantRepository.deleteById(id);
            log.info("Consultant deleted successfully: {}", id);
            return AdminActionResponse.builder()
                    .message("Consultant deleted successfully")
                    .success(true)
                    .action("DELETE_CONSULTANT")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/consultants/{id}/block")
    public AdminActionResponse blockConsultant(@PathVariable UUID id) {
        log.info("PUT /api/admin/consultants/{}/block", id);
        
        try {
            Consultant consultant = consultantRepository.findById(id).orElseThrow();
            log.debug("Found consultant to block: {}", consultant.getUserId());
            userService.setUserBlocked(consultant.getUserId(), true);
            log.info("Consultant blocked successfully: {} (user: {})", id, consultant.getUserId());
            return AdminActionResponse.builder()
                    .message("Consultant blocked successfully")
                    .success(true)
                    .action("BLOCK_CONSULTANT")
                    .build();
        } catch (Exception e) {
            log.error("Error blocking consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/consultants/{id}/unblock")
    public AdminActionResponse unblockConsultant(@PathVariable UUID id) {
        log.info("PUT /api/admin/consultants/{}/unblock", id);
        
        try {
            Consultant consultant = consultantRepository.findById(id).orElseThrow();
            log.debug("Found consultant to unblock: {}", consultant.getUserId());
            userService.setUserBlocked(consultant.getUserId(), false);
            log.info("Consultant unblocked successfully: {} (user: {})", id, consultant.getUserId());
            return AdminActionResponse.builder()
                    .message("Consultant unblocked successfully")
                    .success(true)
                    .action("UNBLOCK_CONSULTANT")
                    .build();
        } catch (Exception e) {
            log.error("Error unblocking consultant {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
} 