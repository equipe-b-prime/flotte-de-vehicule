package com.flottevehicule.flottevehicule.entity;

import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Représente l'affectation d'un véhicule à un salarié pour une période donnée.
 */
@Entity
@Table(name = "affectation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Affectation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "kilometrage_restitution")
    private Integer kilometrageRestitution;

    @ManyToOne
    @JoinColumn(name = "salarie_id")
    private Salarie salarie;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;
}