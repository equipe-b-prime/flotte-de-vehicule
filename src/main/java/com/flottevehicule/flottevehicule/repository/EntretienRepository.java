package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Entretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    List<Entretien> findAllByOrderByDateInterventionDesc();
    List<Entretien> findByVehiculeIdOrderByDateInterventionDesc(Long vehiculeId);

    @Query("SELECT MAX(e.kilometrage) FROM Entretien e WHERE e.vehicule.id = :vehiculeId")
    Integer findDernierKilometrage(@Param("vehiculeId") Long vehiculeId);
}