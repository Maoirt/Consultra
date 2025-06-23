package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consultant_documents")
public class ConsultantDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "consultant_id", nullable = false)
    private UUID consultantId;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "file_url", nullable = false, length = 256)
    private String fileUrl;

    @Column(name = "description", nullable = false, length = 150)
    private String description;
} 