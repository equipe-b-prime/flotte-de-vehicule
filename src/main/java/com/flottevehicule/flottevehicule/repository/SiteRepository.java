package com.flottevehicule.flottevehicule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flottevehicule.flottevehicule.entity.Site;

/**
 * Repository JPA pour l'accès aux données des sites.
 * Fournit les opérations CRUD standard sur la table "site".
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
}