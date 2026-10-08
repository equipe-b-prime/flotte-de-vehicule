package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un salarié de l'entreprise.
 */
@Entity
@Table(name = "salarie")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salarie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private String service;
}