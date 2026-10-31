package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un site.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String ville;
    private String adresse;
}