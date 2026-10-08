package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Salarie;

/**
 * Repository JPA pour l'accès aux données des salariés.
 * Fournit les opérations CRUD standard sur la table "salarie".
 */
@Repository
public interface SalarieRepository extends JpaRepository<Salarie, Long> {
}