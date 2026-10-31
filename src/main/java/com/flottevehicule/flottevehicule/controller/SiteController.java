package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.KilometrageSiteResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.service.RestitutionReportingService;
import com.flottevehicule.flottevehicule.service.SiteService;
import com.flottevehicule.flottevehicule.service.VehiculeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des sites et du reporting kilométrique associé.
 * Délègue l'intégralité des traitements métiers à {@link SiteService}, {@link VehiculeService}
 * et {@link RestitutionReportingService}.
 */
@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@Tag(name = "Sites", description = "Gestion des sites et du suivi du kilométrage/véhicules associés")
public class SiteController {

    private final SiteService siteService;
    private final VehiculeService vehiculeService;
    private final RestitutionReportingService restitutionReportingService;

    /**
     * Crée un nouveau site avec nom unique obligatoire.
     *
     * @param siteRequest les informations du site à créer
     * @return le site créé
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau site",
               description = "F1 - Création d'un site avec nom unique, ville et adresse obligatoires")
    public SiteResponseDto createSite(@Valid @RequestBody SiteRequestDto siteRequest) {
        return siteService.createSite(siteRequest);
    }

    /**
     * Récupère la liste de tous les sites, triée par ville puis par nom.
     *
     * @return la liste ordonnée des sites
     */
    @GetMapping
    @Operation(summary = "Lister tous les sites",
               description = "F1 - Consultation de la liste des sites triée par ville puis par nom")
    public List<SiteResponseDto> getAllSites() {
        return siteService.getAllSites();
    }

    /**
     * Récupère un site par son identifiant unique.
     *
     * @param id l'identifiant du site
     * @return les détails du site
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un site par son ID",
               description = "F1 - Consultation d'un site par son ID (404 si inconnu)")
    public SiteResponseDto getSiteById(
            @Parameter(description = "Identifiant du site", example = "1")
            @PathVariable Long id) {
        return siteService.getSiteById(id);
    }

    /**
     * Met à jour la ville et l'adresse d'un site existant.
     *
     * @param id l'identifiant du site à modifier
     * @param siteRequest les nouvelles informations
     * @return le site mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un site",
               description = "F1 - Modification de l'adresse et de la ville d'un site existant")
    public SiteResponseDto updateSiteById(
            @Parameter(description = "Identifiant du site à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SiteRequestDto siteRequest) {
        return siteService.updateSiteById(id, siteRequest);
    }

    /**
     * Supprime un site s'il ne possède aucun véhicule rattaché.
     *
     * @param id l'identifiant du site à supprimer
     * @return le site supprimé
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un site",
               description = "F1 - Suppression refusée (409) si des véhicules sont rattachés au site")
    public SiteResponseDto deleteSiteById(
            @Parameter(description = "Identifiant du site à supprimer", example = "1")
            @PathVariable Long id) {
        return siteService.deleteSiteById(id);
    }

    /**
     * Obtient la liste des véhicules rattachés à un site.
     *
     * @param id l'identifiant du site
     * @return la liste des véhicules du site
     */
    @GetMapping("/{id}/vehicules")
    @Operation(summary = "Obtenir la liste des véhicules rattachés à un site",
               description = "F6 - Liste des véhicules d'un site triés par immatriculation")
    public List<VehiculeResponseDto> getVehiculesBySite(
            @Parameter(description = "Identifiant du site", example = "1")
            @PathVariable Long id) {
        return vehiculeService.getVehiculesBySite(id);
    }

    /**
     * Calcule le rapport kilométrique mensuel pour un site via la vue SQL v_km_site_mensuel.
     *
     * @param id l'identifiant du site
     * @param mois le mois d'analyse au format YYYY-MM (optionnel, mois courant par défaut)
     * @return le récapitulatif du kilométrage du site et par véhicule
     */
    @GetMapping("/{id}/kilometrage")
    @Operation(summary = "Obtenir le rapport de kilométrage d'un site (filtrable par mois)",
               description = "F19 - Mesure le kilométrage mensuel par site et par véhicule via la vue SQL v_km_site_mensuel")
    public KilometrageSiteResponseDto getKilometrageParSite(
            @Parameter(description = "Identifiant du site", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Mois d'analyse au format YYYY-MM (ex: 2026-04)", example = "2026-04")
            @RequestParam(required = false) String mois) {
        return restitutionReportingService.getKilometrageParSite(id, mois);
    }
}
