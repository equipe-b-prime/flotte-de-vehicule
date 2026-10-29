package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente l'état d'affectation d'un véhicule (ex: DISPONIBLE, AFFECTE, EN_MAINTENANCE).
 */
@Entity
@Table(name = "etat_affectation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtatAffectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String libelle;
}

