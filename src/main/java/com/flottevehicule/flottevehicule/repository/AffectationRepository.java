package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findAllByOrderByDateDebutDesc();
    List<Affectation> findBySalarieIdOrderByDateDebutDesc(Long salarieId);
    List<Affectation> findByVehiculeIdOrderByDateDebutDesc(Long vehiculeId);
    boolean existsByVehiculeIdAndDateDebutBeforeAndDateFinIsNullOrDateFinAfter(
            Long vehiculeId, LocalDateTime dateDebut, LocalDateTime dateFin);
    boolean existsByVehiculeIdAndDateFinIsNull(Long vehiculeId);
}