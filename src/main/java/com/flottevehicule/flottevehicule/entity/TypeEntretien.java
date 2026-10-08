package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un type d'entretien.
 */
@Entity
@Table(name = "type_entretien")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeEntretien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    @Column(name = "est_regulier")
    private Boolean estRegulier;
}