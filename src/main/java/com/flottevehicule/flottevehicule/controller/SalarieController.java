package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import com.flottevehicule.flottevehicule.service.SalarieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des salariés et de leur historique d'affectations.
 * Délègue l'intégralité des traitements métiers à {@link SalarieService} et {@link AffectationService}.
 */
@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
@Tag(name = "Salariés", description = "Gestion des salariés et de leur historique d'affectations")
public class SalarieController {

    private final SalarieService salarieService;
    private final AffectationService affectationService;

    /**
     * Enregistre un nouveau salarié avec contrôle d'unicité du matricule.
     *
     * @param salarieRequest les informations du salarié à créer
     * @return le salarié créé
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau salarié",
               description = "F3 - Crée un salarié avec matricule unique obligatoire")
    public SalarieResponseDto createSalarie(@Valid @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.createSalarie(salarieRequest);
    }

    /**
     * Récupère la liste de tous les salariés, ordonnée par nom croissant.
     *
     * @return la liste des salariés
     */
    @GetMapping
    @Operation(summary = "Lister tous les salariés",
               description = "F3 - Consultation de tous les salariés triés par nom")
    public List<SalarieResponseDto> getAllSalaries() {
        return salarieService.getAllSalaries();
    }

    /**
     * Récupère les détails d'un salarié par son identifiant unique.
     *
     * @param id l'identifiant du salarié
     * @return le salarié trouvé
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un salarié par son ID",
               description = "F3 - Renvoie le salarié correspondant ou 404 si inconnu")
    public SalarieResponseDto getSalarieById(
            @Parameter(description = "Identifiant du salarié", example = "1")
            @PathVariable Long id) {
        return salarieService.getSalarieById(id);
    }

    /**
     * Met à jour les informations modifiables d'un salarié (service).
     *
     * @param id l'identifiant du salarié à modifier
     * @param salarieRequest les nouvelles informations
     * @return le salarié mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un salarié",
               description = "F3 - Permet de modifier le service du salarié (matricule figé)")
    public SalarieResponseDto updateSalarieById(
            @Parameter(description = "Identifiant du salarié à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.updateSalarieById(id, salarieRequest);
    }

    /**
     * Supprime un salarié s'il n'a pas d'affectation en cours.
     *
     * @param id l'identifiant du salarié à supprimer
     * @return le salarié supprimé
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un salarié",
               description = "F3 - Suppression refusée (409) si le salarié a une affectation en cours")
    public SalarieResponseDto deleteSalarieById(
            @Parameter(description = "Identifiant du salarié à supprimer", example = "1")
            @PathVariable Long id) {
        return salarieService.deleteSalarieById(id);
    }

    /**
     * Obtient la liste des affectations associées à un salarié.
     *
     * @param id l'identifiant du salarié
     * @return la liste des affectations du salarié
     */
    @GetMapping("/{id}/affectations")
    @Operation(summary = "Obtenir la liste des affectations d'un salarié",
               description = "F7 - Historique des affectations d'un salarié avec indication de l'affectation active")
    public List<AffectationResponseDto> getAffectationsBySalarie(
            @Parameter(description = "Identifiant du salarié", example = "1")
            @PathVariable Long id) {
        return affectationService.getAffectationsBySalarie(id);
    }
}
