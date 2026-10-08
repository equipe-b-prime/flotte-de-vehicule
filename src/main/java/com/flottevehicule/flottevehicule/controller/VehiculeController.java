package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.VehiculeService;

/**
 * Contrôleur REST exposant les endpoints de gestion des véhicules.
 */
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {
    private final VehiculeService vehiculeService;

    /**
     * Crée un nouveau véhicule.
     *
     * @param vehiculeRequest les informations du véhicule à créer
     * @return le véhicule créé
     */
    @PostMapping
    public VehiculeResponseDto createVehicule(@RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.createVehicule(vehiculeRequest);
    }

    /**
     * Récupère un véhicule à partir de son identifiant.
     *
     * @param id L'identifiant du véhicule à récupérer.
     * @return VehiculeResponseDto Les détails du véhicule récupéré.
     */
    @GetMapping("/{id}")
    public VehiculeResponseDto getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehiculeById(id);
    }

    /**
     * Récupère la liste de tous les véhicules.
     *
     * @return List<VehiculeResponseDto> La liste des véhicules récupérés.
     */
    @GetMapping
    public List<VehiculeResponseDto> getAllVehicules() {
        return vehiculeService.getAllVehicules();
    }

    /**
     * Modifie un véhicule existant.
     *
     * @param id L'identifiant du véhicule à modifier.
     * @param vehiculeRequest Les données de la requête pour modifier le véhicule.
     * @return VehiculeResponseDto Les détails du véhicule modifié.
     */
    @PutMapping("/{id}")
    public VehiculeResponseDto updateVehiculeById(@PathVariable Long id, @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.updateVehiculeById(id, vehiculeRequest);
    }

    /**
     * Supprime un véhicule à partir de son identifiant.
     *
     * @param id L'identifiant du véhicule à supprimer.
     * @return VehiculeResponseDto Les détails du véhicule supprimé.
     */
    @DeleteMapping("/{id}")
    public VehiculeResponseDto deleteVehiculeById(@PathVariable Long id) {
        return vehiculeService.deleteVehiculeById(id);
    }
}