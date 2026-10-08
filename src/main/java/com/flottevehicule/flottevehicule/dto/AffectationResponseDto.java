package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Réponse contenant les données d'une affectation, avec le salarié et le véhicule associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffectationResponseDto {
    private Long id;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateDebut;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateFin;
    private SalarieResponseDto salarie;
    private VehiculeResponseDto vehicule;
}