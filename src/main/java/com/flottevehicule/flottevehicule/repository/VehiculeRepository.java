package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Vehicule;

/**
 * Repository JPA pour l'accès aux données des véhicules.
 * Fournit les opérations CRUD standard sur la table "vehicule".
 */
@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
}