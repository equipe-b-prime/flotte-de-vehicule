package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des entretiens et opérations de maintenance.
 * Délègue l'intégralité des traitements métiers à {@link EntretienService}.
 */
@RestController
@RequestMapping("/entretiens")
@RequiredArgsConstructor
@Tag(name = "Entretiens", description = "Gestion des opérations de maintenance et d'entretien de la flotte")
public class EntretienController {

    private final EntretienService entretienService;

    /**
     * Enregistre une nouvelle intervention d'entretien pour un véhicule.
     *
     * @param entretienRequest les données de l'entretien (coût, date, kilométrage, prestataire, type)
     * @return l'entretien enregistré
     */
    @PostMapping
    @Operation(summary = "Enregistrer un nouvel entretien",
               description = "F5/F14 - Enregistre une intervention avec vérification de kilométrage croissant et coût > 0")
    public EntretienResponseDto createEntretien(@Valid @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.createEntretien(entretienRequest);
    }

    /**
     * Liste les entretiens de manière paginée avec tri par date décroissante par défaut.
     *
     * @param page numéro de page (0 par défaut)
     * @param size nombre d'éléments par page (20 par défaut, plafonné à 100)
     * @param sort champ et direction de tri (dateIntervention,desc par défaut)
     * @return la page d'entretiens
     */
    @GetMapping
    @Operation(summary = "Lister tous les entretiens (paginé)",
               description = "F10 - Consultation paginée des entretiens avec tri par date décroissante")
    public Page<EntretienResponseDto> getAllEntretiens(
            @Parameter(description = "Numéro de page (index 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri et direction", example = "dateIntervention,desc")
            @RequestParam(defaultValue = "dateIntervention,desc") String sort) {
        return entretienService.getAllEntretiensPaginated(page, size, sort);
    }

    /**
     * Récupère les détails d'un entretien par son identifiant unique.
     *
     * @param id l'identifiant de l'entretien
     * @return les détails de l'entretien
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un entretien par son ID",
               description = "F5 - Renvoie l'entretien correspondant ou 404 si introuvable")
    public EntretienResponseDto getEntretienById(
            @Parameter(description = "Identifiant de l'entretien", example = "1")
            @PathVariable Long id) {
        return entretienService.getEntretienById(id);
    }

    /**
     * Met à jour le coût et/ou le prestataire d'un entretien existant.
     *
     * @param id l'identifiant de l'entretien à modifier
     * @param entretienRequest les nouvelles données
     * @return l'entretien mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un entretien existant",
               description = "F5 - Permet de modifier le coût et le prestataire d'un entretien (date et véhicule figés)")
    public EntretienResponseDto updateEntretienById(
            @Parameter(description = "Identifiant de l'entretien", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.updateEntretienById(id, entretienRequest);
    }

    /**
     * Supprime un enregistrement d'entretien si la période d'immobilisation n'a pas débuté.
     *
     * @param id l'identifiant de l'entretien à supprimer
     * @return l'entretien supprimé
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un enregistrement d'entretien",
               description = "F5 - Supprime un entretien (autorisé si l'intervention n'a pas encore eu lieu)")
    public EntretienResponseDto deleteEntretienById(
            @Parameter(description = "Identifiant de l'entretien à supprimer", example = "1")
            @PathVariable Long id) {
        return entretienService.deleteEntretienById(id);
    }
}
