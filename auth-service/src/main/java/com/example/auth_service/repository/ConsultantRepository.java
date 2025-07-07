package com.example.auth_service.repository;

import com.example.auth_service.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {
    Optional<Consultant> findByUserId(UUID userId);

    @Query("SELECT c FROM Consultant c WHERE (:profession IS NULL OR c.profession = :profession) " +
            "AND (:minPrice IS NULL OR EXISTS (SELECT s FROM ConsultantServices s WHERE s.consultantId = c.id AND s.price >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (SELECT s FROM ConsultantServices s WHERE s.consultantId = c.id AND s.price <= :maxPrice)) " +
            "AND (:specializationId IS NULL OR EXISTS (SELECT cs FROM ConsultantToSpecialization cs WHERE cs.consultantId = c.id AND cs.specializationId = :specializationId))")
    List<Consultant> searchConsultants(@Param("profession") String profession,
                                       @Param("specializationId") UUID specializationId,
                                       @Param("minPrice") Integer minPrice,
                                       @Param("maxPrice") Integer maxPrice);

    @Query("SELECT DISTINCT c.profession FROM Consultant c WHERE c.profession IS NOT NULL")
    List<String> findAllProfessions();
} 