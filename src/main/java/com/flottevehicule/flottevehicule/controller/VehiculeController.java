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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final EntretienService entretienService;
    private final RestitutionReportingService restitutionReportingService;

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

    @GetMapping("/disponibles")
    public VehiculesDisponiblesResponseDto getVehiculesDisponibles(
            @RequestParam(required = false) String du,
            @RequestParam(required = false) String au) {
        return restitutionReportingService.getVehiculesDisponibles(du, au);
    }

    @GetMapping("/{id}")
    public VehiculeResponseDto getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehiculeById(id);
    }

    @GetMapping("/{id}/cout")
    public CoutVehiculeResponseDto getCoutTotalVehicule(@PathVariable Long id) {
        return restitutionReportingService.getCoutTotalVehicule(id);
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
