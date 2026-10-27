package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalarieRepository extends JpaRepository<Salarie, Long> {
    Optional<Salarie> findByMatricule(String matricule);
    List<Salarie> findAllByOrderByNomAsc();
}