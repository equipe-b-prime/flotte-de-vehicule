package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.EtatAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatAffectationRepository extends JpaRepository<EtatAffectation, Long> {
    Optional<EtatAffectation> findByCode(String code);
}

