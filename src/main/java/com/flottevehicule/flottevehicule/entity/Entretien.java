package com.flottevehicule.flottevehicule.entity;

import com.flottevehicule.flottevehicule.entity.Prestataire;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Représente un entretien subi par un véhicule.
 */
@Entity
@Table(name = "entretien")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entretien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_intervention")
    private LocalDateTime dateIntervention;

    private BigDecimal cout;

    private int dureeImmobilisatin;

    private Integer kilometrage;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "type_entretien_id")
    private TypeEntretien typeEntretien;

    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;
}