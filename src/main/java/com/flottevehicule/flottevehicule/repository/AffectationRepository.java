package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findAllByOrderByDateDebutDesc();
    List<Affectation> findBySalarieIdOrderByDateDebutDesc(Long salarieId);
    List<Affectation> findByVehiculeIdOrderByDateDebutDesc(Long vehiculeId);

    /**
     * Recherche les affectations existantes sur un véhicule qui chevauchent la période demandée.
     * Gère 4 cas :
     *  1) Les deux périodes ont une date de fin → chevauchement classique
     *  2) L'existante est ouverte (dateFin NULL) → bloque toute période postérieure à son début
     *  3) La nouvelle est ouverte (newFin NULL) → chevauche si l'existante finit après newDebut
     *  4) Les deux sont ouvertes → chevauchement systématique
     */
    @Query("SELECT a FROM Affectation a WHERE a.vehicule.id = :vehiculeId " +
           "AND (a.dateFin IS NULL OR a.dateFin > :newDebut) " +
           "AND (a.dateDebut < :newFin OR :newFin IS NULL)")
    List<Affectation> findChevauchements(
            @Param("vehiculeId") Long vehiculeId,
            @Param("newDebut") LocalDateTime newDebut,
            @Param("newFin") LocalDateTime newFin);
}