package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Requête d'enregistrement de la restitution d'un véhicule (clôture d'affectation).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestitutionRequestDto extends AbstractClean {

    @Schema(description = "Date effective de restitution (format : dd-MM-yyyy HH:mm:ss)", example = "01-06-2023 18:00:00", type = "string")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateRestitution;

    @Schema(description = "Kilométrage relevé au retour (doit être >= dernier kilométrage connu)", example = "12500")
    private Integer kilometrage;
}
