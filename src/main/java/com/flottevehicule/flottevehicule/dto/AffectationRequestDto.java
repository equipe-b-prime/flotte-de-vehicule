package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Requête de création ou de modification d'une affectation.
 * Le salarié et le véhicule sont transmis via leurs identifiants.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffectationRequestDto extends AbstractClean implements RequestDto {

    @Schema(description = "Date de début de l'affectation (format : dd-MM-yyyy HH:mm:ss)", example = "01-01-2023 00:00:00", type = "string")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateDebut;

    @Schema(description = "Date de fin de l'affectation (format : dd-MM-yyyy HH:mm:ss, null si en cours)", example = "01-06-2023 18:00:00", type = "string")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateFin;

    @Schema(description = "Identifiant du salarié", example = "1")
    private Long salarieId;

    @Schema(description = "Identifiant du véhicule", example = "1")
    private Long vehiculeId;
}