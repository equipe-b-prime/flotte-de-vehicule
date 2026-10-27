package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Optional<Site> findByNom(String nom);
    List<Site> findAllByOrderByVilleAscNomAsc();
    boolean existsByVehiculesIsNotEmpty();
}