package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_vehicule_stats")
@Immutable
@Getter
@NoArgsConstructor
public class VehiculeStatsView {

    @Id
    private Long vehiculeId;
    private Long nbAffectations;
    private Long nbEntretiens;
    private BigDecimal coutTotalEntretiens;
    private Long joursImmobilises;
    private LocalDateTime premierEntretien;
    private LocalDateTime dernierEntretien;
}

