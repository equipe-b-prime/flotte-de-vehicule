package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    Optional<Vehicule> findByImmatriculation(String immatriculation);
    List<Vehicule> findAllByOrderByImmatriculationAsc();
    List<Vehicule> findBySiteIdOrderByImmatriculationAsc(Long siteId);
    List<Vehicule> findByImmatriculationContainingIgnoreCaseAndMarqueContainingIgnoreCase(
            String immatriculation, String marque);
    List<Vehicule> findByImmatriculationContainingIgnoreCase(String immatriculation);
    List<Vehicule> findByMarqueContainingIgnoreCase(String marque);
    boolean existsBySiteId(Long siteId);
    boolean existsByAffectationsIsNotEmptyOrEntretiensIsNotEmpty();
    Optional<Vehicule> findTopByOrderByKilometrageActuelDesc();
}