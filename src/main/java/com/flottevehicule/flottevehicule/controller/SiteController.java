package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import com.flottevehicule.flottevehicule.service.SiteService;
import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;
    private final VehiculeService vehiculeService;

    @PostMapping
    public SiteResponseDto createSite(@RequestBody SiteRequestDto siteRequest) {
        return siteService.createSite(siteRequest);
    }

    @GetMapping("/{id}")
    public SiteResponseDto getSiteById(@PathVariable Long id) {
        return siteService.getSiteById(id);
    }

    @GetMapping
    public List<SiteResponseDto> getAllSites() {
        return siteService.getAllSites();
    }

    @PutMapping("/{id}")
    public SiteResponseDto updateSiteById(@PathVariable Long id, @RequestBody SiteRequestDto siteRequest) {
        return siteService.updateSiteById(id, siteRequest);
    }

    @DeleteMapping("/{id}")
    public SiteResponseDto deleteSiteById(@PathVariable Long id) {
        return siteService.deleteSiteById(id);
    }

    @GetMapping("/{id}/vehicules")
    public List<VehiculeResponseDto> getVehiculesBySite(@PathVariable Long id) {
        return vehiculeService.getVehiculesBySite(id);
    }
}