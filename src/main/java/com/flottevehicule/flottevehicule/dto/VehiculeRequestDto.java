package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Requête de création ou de modification d'un véhicule.
 * Le site et le contrat associés sont transmis via leurs identifiants.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculeRequestDto {
    private Long id;
    private String nom;
    private String immatriculation;
    private String marque;
    private String modele;
    private LocalDateTime dateMiseEnCirculation;
    private Long siteId;
    private Long contratId;
}