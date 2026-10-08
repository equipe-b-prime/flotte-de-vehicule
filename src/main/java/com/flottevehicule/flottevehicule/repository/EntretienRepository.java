package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Entretien;

/**
 * Repository JPA pour l'accès aux données des entretiens.
 * Fournit les opérations CRUD standard sur la table "entretien".
 */
@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Long> {
}