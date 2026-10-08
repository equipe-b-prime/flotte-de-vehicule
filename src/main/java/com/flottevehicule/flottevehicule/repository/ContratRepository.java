package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Contrat;

/**
 * Repository JPA pour l'accès aux données des contrats.
 * Fournit les opérations CRUD standard sur la table "contrat".
 */
@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {
}