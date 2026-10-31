package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Requête de création ou de modification d'un entretien.
 * Le véhicule, le type d'entretien et le prestataire sont transmis via leurs identifiants.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntretienRequestDto extends AbstractClean implements RequestDto {
    private Long id;

    @Schema(description = "Date de l'intervention (format : dd-MM-yyyy HH:mm:ss)", example = "01-07-2023 10:00:00", type = "string")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateIntervention;

    @Schema(description = "Coût de l'entretien (doit être > 0)", example = "250.50")
    private BigDecimal cout;

    @Schema(description = "Durée d'immobilisation en jours", example = "2")
    private Integer dureeImmobilisatin;

    @Schema(description = "Kilométrage du véhicule lors de l'intervention (doit être >= dernier connu)", example = "20000")
    private Integer kilometrage;

    @Schema(description = "Identifiant du véhicule concerné", example = "1")
    private Long vehiculeId;

    @Schema(description = "Identifiant du type d'entretien", example = "1")
    private Long typeEntretienId;

    @Schema(description = "Identifiant du prestataire", example = "1")
    private Long prestataireId;
}