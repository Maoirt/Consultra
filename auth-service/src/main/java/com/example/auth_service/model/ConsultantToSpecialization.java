package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consultant_to_specialization")
@IdClass(ConsultantToSpecialization.PK.class)
public class ConsultantToSpecialization {
    @Id
    @Column(name = "consultant_id")
    private UUID consultantId;

    @Id
    @Column(name = "specialization_id")
    private UUID specializationId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private UUID consultantId;
        private UUID specializationId;
    }
} 