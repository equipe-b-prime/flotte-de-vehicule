package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un prestataire réalisant les entretiens.
 */
@Entity
@Table(name = "prestataire")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prestataire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
}