package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un type de dépense.
 */
@Entity
@Table(name = "type_depense")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeDepense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;
}