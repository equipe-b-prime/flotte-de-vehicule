package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import com.flottevehicule.flottevehicule.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final EntretienService entretienService;

    @PostMapping
    public VehiculeResponseDto createVehicule(@RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.createVehicule(vehiculeRequest);
    }

    @GetMapping
    public List<VehiculeResponseDto> getAllVehicules(
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque) {
        return vehiculeService.searchVehicules(immatriculation, marque);
    }

    @GetMapping("/{id}")
    public VehiculeResponseDto getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehiculeById(id);
    }

    @PutMapping("/{id}")
    public VehiculeResponseDto updateVehiculeById(@PathVariable Long id, @RequestBody VehiculeRequestDto vehiculeRequest) {
        return vehiculeService.updateVehiculeById(id, vehiculeRequest);
    }

    @DeleteMapping("/{id}")
    public VehiculeResponseDto deleteVehiculeById(@PathVariable Long id) {
        return vehiculeService.deleteVehiculeById(id);
    }

    @GetMapping("/{id}/entretiens")
    public List<EntretienResponseDto> getEntretiensByVehicule(@PathVariable Long id) {
        return entretienService.getEntretiensByVehicule(id);
    }

    @GetMapping("/{id}/prochain-entretien")
    public ProchainEntretienResponseDto getProchainEntretien(@PathVariable Long id) {
        return vehiculeService.calculerProchainEntretien(id);
    }
}
