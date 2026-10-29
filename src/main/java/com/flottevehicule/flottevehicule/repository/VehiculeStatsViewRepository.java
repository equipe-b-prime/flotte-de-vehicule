package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.VehiculeStatsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculeStatsViewRepository extends JpaRepository<VehiculeStatsView, Long> {
    Optional<VehiculeStatsView> findByVehiculeId(Long vehiculeId);
}

