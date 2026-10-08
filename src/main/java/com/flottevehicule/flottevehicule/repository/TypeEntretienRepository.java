package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;

/**
 * Repository JPA pour l'accès aux données des types d'entretien.
 * Fournit les opérations CRUD standard sur la table "type_entretien".
 */
@Repository
public interface TypeEntretienRepository extends JpaRepository<TypeEntretien, Long> {
}