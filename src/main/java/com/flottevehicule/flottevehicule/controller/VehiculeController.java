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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion du parc automobile, de la recherche, de la disponibilité,
 * des coûts d'entretien et des prévisions de maintenance.
 * Délègue l'intégralité des traitements métiers à {@link VehiculeService}, {@link EntretienService}
 * et {@link RestitutionReportingService}.
 */
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion du parc automobile, de la disponibilité, des coûts et de la maintenance")
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final EntretienService entretienService;
    private final RestitutionReportingService restitutionReportingService;

    /**
     * Enregistre un nouveau véhicule dans le parc automobile.
     *
     * @param vehiculeRequest les caractéristiques du véhicule (immatriculation, marque, modèle, date, site, contrat)
     * @return le véhicule créé
     */
    @PostMapping
    @Operation(summary = "Créer et enregistrer un nouveau véhicule",
               description = "F2 - Enregistrement d'un véhicule avec immatriculation unique et date de mise en service non future")
    public VehiculeResponseDto createVehicule(@Valid @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.createVehicule(vehiculeRequest);
    }

    /**
     * Recherche des véhicules selon des critères optionnels (immatriculation, marque) ou liste complète.
     *
     * @param immatriculation filtre partiel insensible à la casse sur l'immatriculation (optionnel)
     * @param marque filtre partiel insensible à la casse sur la marque (optionnel)
     * @return la liste des véhicules correspondants
     */
    @GetMapping
    @Operation(summary = "Rechercher des véhicules (filtrage optionnel par immatriculation ou marque)",
               description = "F9 - Moteur de recherche multicritères combinables ou consultation de la liste complète")
    public List<VehiculeResponseDto> getAllVehicules(
            @Parameter(description = "Filtre par immatriculation (partiel ou complet, insensible à la casse)", example = "AA-100")
            @RequestParam(required = false) String immatriculation,
            @Parameter(description = "Filtre par marque du véhicule", example = "Peugeot")
            @RequestParam(required = false) String marque) {
        return vehiculeService.searchVehicules(immatriculation, marque);
    }

    /**
     * Liste les véhicules disponibles sur une période temporelle donnée, groupés par site.
     *
     * @param du date et heure de début de période (format dd-MM-yyyy HH:mm:ss, optionnel)
     * @param au date et heure de fin de période (format dd-MM-yyyy HH:mm:ss, optionnel)
     * @return les véhicules disponibles groupés par site avec indicateurs de maintenance
     */
    @GetMapping("/disponibles")
    @Operation(summary = "Consulter les véhicules disponibles sur une plage de dates",
               description = "F17 - Recherche dynamique des véhicules sans affectation ni immobilisation sur la période")
    public VehiculesDisponiblesResponseDto getVehiculesDisponibles(
            @Parameter(description = "Date de début (format : dd-MM-yyyy HH:mm:ss, ex: 01-11-2026 00:00:00)", example = "01-11-2026 00:00:00")
            @RequestParam(required = false) String du,
            @Parameter(description = "Date de fin (format : dd-MM-yyyy HH:mm:ss, ex: 05-11-2026 23:59:59)", example = "05-11-2026 23:59:59")
            @RequestParam(required = false) String au) {
        return restitutionReportingService.getVehiculesDisponibles(du, au);
    }

    /**
     * Récupère les détails complets d'un véhicule par son identifiant unique.
     *
     * @param id l'identifiant du véhicule
     * @return les détails du véhicule
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un véhicule par son ID",
               description = "F2 - Consultation d'un véhicule par son identifiant (404 si introuvable)")
    public VehiculeResponseDto getVehiculeById(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id) {
        return vehiculeService.getVehiculeById(id);
    }

    /**
     * Calcule le coût global d'un véhicule et sa ventilation par type d'intervention via les vues SQL.
     *
     * @param id l'identifiant du véhicule
     * @return le bilan financier du véhicule (coût total, coût par kilomètre, détail par type)
     */
    @GetMapping("/{id}/cout")
    @Operation(summary = "Obtenir le calcul du coût total d'un véhicule",
               description = "F18 - Exploite les vues SQL v_vehicule_stats et v_vehicule_cout_type pour calculer le bilan financier")
    public CoutVehiculeResponseDto getCoutTotalVehicule(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id) {
        return restitutionReportingService.getCoutTotalVehicule(id);
    }

    /**
     * Met à jour les informations modifiables d'un véhicule (notamment son site de rattachement).
     *
     * @param id l'identifiant du véhicule à modifier
     * @param vehiculeRequest les nouvelles données
     * @return le véhicule mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un véhicule",
               description = "F2 - Permet notamment le transfert de site (l'immatriculation et la date restent figées)")
    public VehiculeResponseDto updateVehiculeById(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.updateVehiculeById(id, vehiculeRequest);
    }

    /**
     * Supprime un véhicule du parc s'il ne possède ni affectation ni entretien historique.
     *
     * @param id l'identifiant du véhicule à supprimer
     * @return le véhicule supprimé
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule du parc",
               description = "F2 - Suppression refusée (409) si le véhicule possède des affectations ou entretiens historiques")
    public VehiculeResponseDto deleteVehiculeById(
            @Parameter(description = "Identifiant du véhicule à supprimer", example = "1")
            @PathVariable Long id) {
        return vehiculeService.deleteVehiculeById(id);
    }

    /**
     * Obtient l'historique complet des entretiens d'un véhicule.
     *
     * @param id l'identifiant du véhicule
     * @return la liste des entretiens passés du véhicule
     */
    @GetMapping("/{id}/entretiens")
    @Operation(summary = "Obtenir l'historique des entretiens d'un véhicule",
               description = "F8 - Liste chronologique des entretiens avec affichage du coût cumulé")
    public List<EntretienResponseDto> getEntretiensByVehicule(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id) {
        return entretienService.getEntretiensByVehicule(id);
    }

    /**
     * Calcule la projection et l'échéance du prochain entretien recommandé pour un véhicule.
     *
     * @param id l'identifiant du véhicule
     * @return les données prévisionnelles du prochain entretien
     */
    @GetMapping("/{id}/prochain-entretien")
    @Operation(summary = "Calculer l'échéance du prochain entretien d'un véhicule",
               description = "F15 - Algorithme de calcul prédictif basé sur le kilométrage et l'intervalle recommandé")
    public ProchainEntretienResponseDto getProchainEntretien(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id) {
        return vehiculeService.calculerProchainEntretien(id);
    }
}
