package com.flottevehicule.flottevehicule.dto;

import com.flottevehicule.flottevehicule.dto.ContratResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Réponse contenant les données d'un véhicule, avec le site et le contrat associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculeResponseDto {
    private Long id;
    private String nom;
    private String immatriculation;
    private String marque;
    private String modele;
    private LocalDateTime dateMiseEnCirculation;
    private SiteResponseDto site;
    private ContratResponseDto contrat;
}