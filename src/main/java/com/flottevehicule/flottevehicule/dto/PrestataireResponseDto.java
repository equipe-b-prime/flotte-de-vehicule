package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un prestataire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrestataireResponseDto {
    private Long id;
    private String nom;
}