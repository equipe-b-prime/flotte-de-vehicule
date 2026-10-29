package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.VehiculeCoutTypeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculeCoutTypeViewRepository extends JpaRepository<VehiculeCoutTypeView, Long> {
    List<VehiculeCoutTypeView> findByVehiculeId(Long vehiculeId);
}

