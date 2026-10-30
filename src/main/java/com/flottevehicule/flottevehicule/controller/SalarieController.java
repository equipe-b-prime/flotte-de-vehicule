package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import com.flottevehicule.flottevehicule.service.SalarieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
@Tag(name = "Salariés", description = "Gestion des salariés et de leur historique d'affectations")
public class SalarieController {

    private final SalarieService salarieService;
    private final AffectationService affectationService;

    @PostMapping
    @Operation(summary = "Créer un nouveau salarié")
    public SalarieResponseDto createSalarie(@Valid @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.createSalarie(salarieRequest);
    }

    @GetMapping
    @Operation(summary = "Lister tous les salariés")
    public List<SalarieResponseDto> getAllSalaries() {
        return salarieService.getAllSalaries();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un salarié par son ID")
    public SalarieResponseDto getSalarieById(@PathVariable Long id) {
        return salarieService.getSalarieById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un salarié")
    public SalarieResponseDto updateSalarieById(@PathVariable Long id, @Valid @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.updateSalarieById(id, salarieRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un salarié")
    public SalarieResponseDto deleteSalarieById(@PathVariable Long id) {
        return salarieService.deleteSalarieById(id);
    }

    @GetMapping("/{id}/affectations")
    @Operation(summary = "Obtenir la liste des affectations d'un salarié")
    public List<AffectationResponseDto> getAffectationsBySalarie(@PathVariable Long id) {
        return affectationService.getAffectationsBySalarie(id);
    }
}