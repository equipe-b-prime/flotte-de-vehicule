package com.flottevehicule.flottevehicule.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Requête de création ou de modification d'un salarié.
 * Le matricule doit être unique au sein de l'entreprise.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(onConstructor_ = @JsonCreator)
public class SalarieRequestDto extends AbstractClean implements RequestDto {
    private Long id;

    @Schema(description = "Nom de famille du salarié", example = "Kouassi")
    private String nom;

    @Schema(description = "Prénom du salarié", example = "Jean")
    private String prenom;

    @Schema(description = "Matricule unique du salarié", example = "MAT-001")
    private String matricule;

    @Schema(description = "Service / département de rattachement", example = "Logistique")
    private String service;

    @JsonSetter("nom")
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter("prenom")
    public void setPrenom(String prenom) {
        this.prenom = cleanText(prenom);
    }

    @JsonSetter("matricule")
    public void setMatricule(String matricule) {
        this.matricule = cleanText(matricule);
    }

    @JsonSetter("service")
    public void setService(String service) {
        this.service = cleanText(service);
    }
}
