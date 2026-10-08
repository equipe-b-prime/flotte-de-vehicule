package com.flottevehicule.flottevehicule.dto;

import lombok.*;

/**
 * Requête de création ou de modification d'un site.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteRequestDto {
    private Long id;
    private String nom;
    private String ville;
    private String adresse;
}