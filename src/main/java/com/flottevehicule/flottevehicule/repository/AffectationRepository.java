package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Affectation;

/**
 * Repository JPA pour l'accès aux données des affectations.
 * Fournit les opérations CRUD standard sur la table "affectation".
 */
@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
}