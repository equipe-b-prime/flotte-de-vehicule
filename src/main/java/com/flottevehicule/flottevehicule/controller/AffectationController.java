package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.RestitutionRequestDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/affectations")
@RequiredArgsConstructor
@Tag(name = "Affectations", description = "Gestion des affectations et restitutions de véhicules")
public class AffectationController {

    private final AffectationService affectationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle affectation")
    public AffectationResponseDto createAffectation(@Valid @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.createAffectation(affectationRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une affectation par son ID")
    public AffectationResponseDto getAffectationById(@PathVariable Long id) {
        return affectationService.getAffectationById(id);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les affectations")
    public List<AffectationResponseDto> getAllAffectations() {
        return affectationService.getAllAffectations();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une affectation")
    public AffectationResponseDto updateAffectationById(@PathVariable Long id, @Valid @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.updateAffectationById(id, affectationRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une affectation")
    public AffectationResponseDto deleteAffectationById(@PathVariable Long id) {
        return affectationService.deleteAffectationById(id);
    }

    @PostMapping("/{id}/restitution")
    @Operation(summary = "Enregistrer la restitution d'un véhicule")
    public AffectationResponseDto restituerAffectation(@PathVariable Long id, @Valid @RequestBody RestitutionRequestDto restitutionRequest) {
        return affectationService.restituerAffectation(id, restitutionRequest);
    }
}