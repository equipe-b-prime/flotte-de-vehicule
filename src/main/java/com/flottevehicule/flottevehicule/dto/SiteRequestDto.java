package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'un site.
 * Le nom du site doit être unique.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(onConstructor_ = @JsonCreator)
public class SiteRequestDto extends AbstractClean implements RequestDto {
    private Long id;

    @Schema(description = "Nom unique du site", example = "Site Yopougon")
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Schema(description = "Ville où se situe le site", example = "Abidjan")
    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @Schema(description = "Adresse géographique du site", example = "Boulevard Principal, Yopougon")
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @JsonSetter("nom")
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter("ville")
    public void setVille(String ville) {
        this.ville = cleanText(ville);
    }

    @JsonSetter("adresse")
    public void setAdresse(String adresse) {
        this.adresse = cleanText(adresse);
    }
}
