package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.AffectationService;

/**
 * Contrôleur REST exposant les endpoints de gestion des affectations.
 */
@RestController
@RequestMapping("/affectations")
@RequiredArgsConstructor
public class AffectationController {
    private final AffectationService affectationService;

    /**
     * Crée une nouvelle affectation.
     *
     * @param affectationRequest les informations de l'affectation à créer
     * @return l'affectation créée
     */
    @PostMapping
    public AffectationResponseDto createAffectation(@RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.createAffectation(affectationRequest);
    }

    /**
     * Récupère une affectation à partir de son identifiant.
     *
     * @param id L'identifiant de l'affectation à récupérer.
     * @return AffectationResponseDto Les détails de l'affectation récupérée.
     */
    @GetMapping("/{id}")
    public AffectationResponseDto getAffectationById(@PathVariable Long id) {
        return affectationService.getAffectationById(id);
    }

    /**
     * Récupère la liste de toutes les affectations.
     *
     * @return List<AffectationResponseDto> La liste des affectations récupérées.
     */
    @GetMapping
    public List<AffectationResponseDto> getAllAffectations() {
        return affectationService.getAllAffectations();
    }

    /**
     * Modifie une affectation existante.
     *
     * @param id L'identifiant de l'affectation à modifier.
     * @param affectationRequest Les données de la requête pour modifier l'affectation.
     * @return AffectationResponseDto Les détails de l'affectation modifiée.
     */
    @PutMapping("/{id}")
    public AffectationResponseDto updateAffectationById(@PathVariable Long id, @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.updateAffectationById(id, affectationRequest);
    }

    /**
     * Supprime une affectation à partir de son identifiant.
     *
     * @param id L'identifiant de l'affectation à supprimer.
     * @return AffectationResponseDto Les détails de l'affectation supprimée.
     */
    @DeleteMapping("/{id}")
    public AffectationResponseDto deleteAffectationById(@PathVariable Long id) {
        return affectationService.deleteAffectationById(id);
    }
}