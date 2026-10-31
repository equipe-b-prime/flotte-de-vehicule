package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
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
@NoArgsConstructor(onConstructor_ = @JsonCreator)
public class VehiculeRequestDto extends AbstractClean implements RequestDto {
    private Long id;
    private String nom;
    private String immatriculation;
    private String marque;
    private String modele;

    @Schema(description = "Date de mise en circulation (format : dd-MM-yyyy HH:mm:ss)", example = "01-01-2022 10:00:00", type = "string")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateMiseEnCirculation;

    private Long siteId;
    private Long contratId;

    @JsonSetter("nom")
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter("immatriculation")
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = cleanText(immatriculation);
    }

    @JsonSetter("marque")
    public void setMarque(String marque) {
        this.marque = cleanText(marque);
    }

    @JsonSetter("modele")
    public void setModele(String modele) {
        this.modele = cleanText(modele);
    }
}
