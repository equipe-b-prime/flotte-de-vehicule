package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Requête de création ou de modification d'un salarié.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalarieRequestDto {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private String service;
}