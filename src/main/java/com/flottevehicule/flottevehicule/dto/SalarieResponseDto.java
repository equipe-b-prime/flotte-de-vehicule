package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un salarié.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalarieResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private String service;
}