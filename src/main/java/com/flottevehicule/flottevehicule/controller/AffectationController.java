package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.RestitutionRequestDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des affectations et des restitutions de véhicules.
 * Délègue l'intégralité des traitements métiers à {@link AffectationService}.
 */
@RestController
@RequestMapping("/affectations")
@RequiredArgsConstructor
@Tag(name = "Affectations", description = "Gestion des affectations et restitutions de véhicules")
public class AffectationController {

    private final AffectationService affectationService;

    /**
     * Crée une nouvelle affectation de véhicule pour un salarié.
     *
     * @param affectationRequest les données de l'affectation à créer
     * @return les informations de l'affectation créée
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle affectation",
               description = "F4/F11/F12 - Enregistre une affectation avec contrôles de chevauchement et d'immobilisation")
    public AffectationResponseDto createAffectation(@Valid @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.createAffectation(affectationRequest);
    }

    /**
     * Récupère une affectation par son identifiant unique.
     *
     * @param id l'identifiant de l'affectation
     * @return les détails de l'affectation
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une affectation par son ID",
               description = "F4 - Renvoie l'affectation correspondante ou 404 si introuvable")
    public AffectationResponseDto getAffectationById(
            @Parameter(description = "Identifiant de l'affectation", example = "1")
            @PathVariable Long id) {
        return affectationService.getAffectationById(id);
    }

    /**
     * Liste toutes les affectations enregistrées, triées par date de début décroissante.
     *
     * @return la liste des affectations
     */
    @GetMapping
    @Operation(summary = "Lister toutes les affectations",
               description = "F4 - Renvoie la liste complète ordonnée par date de début décroissante")
    public List<AffectationResponseDto> getAllAffectations() {
        return affectationService.getAllAffectations();
    }

    /**
     * Met à jour une affectation existante (notamment la date de fin).
     *
     * @param id l'identifiant de l'affectation
     * @param affectationRequest les nouvelles données
     * @return l'affectation mise à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une affectation",
               description = "F4 - Permet de modifier la date de fin d'une affectation existante")
    public AffectationResponseDto updateAffectationById(
            @Parameter(description = "Identifiant de l'affectation à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.updateAffectationById(id, affectationRequest);
    }

    /**
     * Supprime une affectation si elle n'a pas encore commencé.
     *
     * @param id l'identifiant de l'affectation
     * @return l'affectation supprimée
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une affectation",
               description = "F4 - Suppression autorisée uniquement si l'affectation n'a pas encore débuté")
    public AffectationResponseDto deleteAffectationById(
            @Parameter(description = "Identifiant de l'affectation à supprimer", example = "1")
            @PathVariable Long id) {
        return affectationService.deleteAffectationById(id);
    }

    /**
     * Enregistre la restitution d'un véhicule et clôture l'affectation.
     *
     * @param id l'identifiant de l'affectation à clôturer
     * @param restitutionRequest les détails de la restitution (date et kilométrage)
     * @return l'affectation clôturée
     */
    @PostMapping("/{id}/restitution")
    @Operation(summary = "Enregistrer la restitution d'un véhicule",
               description = "F4/F13 - Clôture formelle avec mise à jour du kilométrage du véhicule")
    public AffectationResponseDto restituerAffectation(
            @Parameter(description = "Identifiant de l'affectation à clôturer", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestitutionRequestDto restitutionRequest) {
        return affectationService.restituerAffectation(id, restitutionRequest);
    }
}
