package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class EntretienRequestDto {
    private Long id;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateIntervention;
    private BigDecimal cout;
    private Integer dureeImmobilisatin;
    private Integer kilometrage;
    private Long vehiculeId;
    private Long typeEntretienId;
    private Long prestataireId;
}