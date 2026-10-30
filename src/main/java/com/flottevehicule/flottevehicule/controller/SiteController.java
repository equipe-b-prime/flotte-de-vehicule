package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.KilometrageSiteResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.service.RestitutionReportingService;
import com.flottevehicule.flottevehicule.service.SiteService;
import com.flottevehicule.flottevehicule.service.VehiculeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@Tag(name = "Sites", description = "Gestion des sites et du suivi du kilométrage/véhicules associés")
public class SiteController {

    private final SiteService siteService;
    private final VehiculeService vehiculeService;
    private final RestitutionReportingService restitutionReportingService;

    @PostMapping
    @Operation(summary = "Créer un nouveau site")
    public SiteResponseDto createSite(@Valid @RequestBody SiteRequestDto siteRequest) {
        return siteService.createSite(siteRequest);
    }

    @GetMapping
    @Operation(summary = "Lister tous les sites")
    public List<SiteResponseDto> getAllSites() {
        return siteService.getAllSites();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un site par son ID")
    public SiteResponseDto getSiteById(@PathVariable Long id) {
        return siteService.getSiteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un site")
    public SiteResponseDto updateSiteById(@PathVariable Long id, @Valid @RequestBody SiteRequestDto siteRequest) {
        return siteService.updateSiteById(id, siteRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un site")
    public SiteResponseDto deleteSiteById(@PathVariable Long id) {
        return siteService.deleteSiteById(id);
    }

    @GetMapping("/{id}/vehicules")
    @Operation(summary = "Obtenir la liste des véhicules rattachés à un site")
    public List<VehiculeResponseDto> getVehiculesBySite(@PathVariable Long id) {
        return vehiculeService.getVehiculesBySite(id);
    }

    @GetMapping("/{id}/kilometrage")
    @Operation(summary = "Obtenir le rapport de kilométrage d'un site (filtrable par mois)")
    public KilometrageSiteResponseDto getKilometrageParSite(
            @PathVariable Long id,
            @RequestParam(required = false) String mois) {
        return restitutionReportingService.getKilometrageParSite(id, mois);
    }
}