package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.CoutVehiculeResponseDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculesDisponiblesResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import com.flottevehicule.flottevehicule.service.RestitutionReportingService;
import com.flottevehicule.flottevehicule.service.VehiculeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion du parc automobile, de la disponibilité, des coûts et de la maintenance")
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final EntretienService entretienService;
    private final RestitutionReportingService restitutionReportingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer et enregistrer un nouveau véhicule")
    public VehiculeResponseDto createVehicule(@Valid @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.createVehicule(vehiculeRequest);
    }

    @GetMapping
    @Operation(summary = "Rechercher des véhicules (filtrage optionnel par immatriculation ou marque)")
    public List<VehiculeResponseDto> getAllVehicules(
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque) {
        return vehiculeService.searchVehicules(immatriculation, marque);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Consulter les véhicules disponibles sur une plage de dates")
    public VehiculesDisponiblesResponseDto getVehiculesDisponibles(
            @RequestParam(required = false) String du,
            @RequestParam(required = false) String au) {
        return restitutionReportingService.getVehiculesDisponibles(du, au);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un véhicule par son ID")
    public VehiculeResponseDto getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehiculeById(id);
    }

    @GetMapping("/{id}/cout")
    @Operation(summary = "Obtenir le calcul du coût total d'un véhicule")
    public CoutVehiculeResponseDto getCoutTotalVehicule(@PathVariable Long id) {
        return restitutionReportingService.getCoutTotalVehicule(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un véhicule")
    public VehiculeResponseDto updateVehiculeById(
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.updateVehiculeById(id, vehiculeRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule du parc")
    public VehiculeResponseDto deleteVehiculeById(@PathVariable Long id) {
        return vehiculeService.deleteVehiculeById(id);
    }

    @GetMapping("/{id}/entretiens")
    @Operation(summary = "Obtenir l'historique des entretiens d'un véhicule")
    public List<EntretienResponseDto> getEntretiensByVehicule(@PathVariable Long id) {
        return entretienService.getEntretiensByVehicule(id);
    }

    @GetMapping("/{id}/prochain-entretien")
    @Operation(summary = "Calculer l'échéance du prochain entretien d'un véhicule")
    public ProchainEntretienResponseDto getProchainEntretien(@PathVariable Long id) {
        return vehiculeService.calculerProchainEntretien(id);
    }
}