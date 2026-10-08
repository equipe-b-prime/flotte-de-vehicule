package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un type d'entretien.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeEntretienResponseDto {
    private Long id;
    private String libelle;
    private Boolean estRegulier;
}