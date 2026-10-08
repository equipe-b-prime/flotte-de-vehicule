package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.SiteService;

/**
 * Contrôleur REST exposant les endpoints de gestion des sites.
 */
@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;

    /**
     * Crée un nouveau site.
     *
     * @param siteRequest les informations du site à créer
     * @return le site créé
     */
    @PostMapping
    public SiteResponseDto createSite(@RequestBody SiteRequestDto siteRequest) {
        return siteService.createSite(siteRequest);
    }

    /**
     * Récupère un site à partir de son identifiant.
     *
     * @param id L'identifiant du site à récupérer.
     * @return SiteResponseDto Les détails du site récupéré.
     */
    @GetMapping("/{id}")
    public SiteResponseDto getSiteById(@PathVariable Long id) {
        return siteService.getSiteById(id);
    }

    /**
     * Récupère la liste de tous les sites.
     *
     * @return List<SiteResponseDto> La liste des sites récupérés.
     */
    @GetMapping
    public List<SiteResponseDto> getAllSites() {
        return siteService.getAllSites();
    }

    /**
     * Modifie un site existant.
     *
     * @param id L'identifiant du site à modifier.
     * @param siteRequest Les données de la requête pour modifier le site.
     * @return SiteResponseDto Les détails du site modifié.
     */
    @PutMapping("/{id}")
    public SiteResponseDto updateSiteById(@PathVariable Long id, @RequestBody SiteRequestDto siteRequest) {
        return siteService.updateSiteById(id, siteRequest);
    }

    /**
     * Supprime un site à partir de son identifiant.
     *
     * @param id L'identifiant du site à supprimer.
     * @return SiteResponseDto Les détails du site supprimé.
     */
    @DeleteMapping("/{id}")
    public SiteResponseDto deleteSiteById(@PathVariable Long id) {
        return siteService.deleteSiteById(id);
    }
}