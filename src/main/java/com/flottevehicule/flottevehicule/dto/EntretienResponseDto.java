package com.flottevehicule.flottevehicule.dto;

import com.flottevehicule.flottevehicule.dto.PrestataireResponseDto;
import com.flottevehicule.flottevehicule.dto.TypeEntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Réponse contenant les données d'un entretien, avec le véhicule,
 * le type d'entretien et le prestataire associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntretienResponseDto {
    private Long id;
    private LocalDateTime dateIntervention;
    private BigDecimal cout;
    private int dureeImmobilisatin;
    private Integer kilometrage;
    private VehiculeResponseDto vehicule;
    private TypeEntretienResponseDto typeEntretien;
    private PrestataireResponseDto prestataire;
}