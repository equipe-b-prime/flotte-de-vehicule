package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Prestataire;

/**
 * Repository JPA pour l'accès aux données des prestataires.
 * Fournit les opérations CRUD standard sur la table "prestataire".
 */
@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {
}