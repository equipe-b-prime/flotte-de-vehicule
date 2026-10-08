package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.repository.SiteRepository;

/**
 * Service métier gérant les sites.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO.
 */
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    /**
     * Convertit une entité site en objet de réponse.
     *
     * @param site L'entité site à convertir.
     * @return SiteResponseDto Les détails du site converti.
     */
    public SiteResponseDto toDto(Site site) {
        return SiteResponseDto.builder()
                .id(site.getId())
                .nom(site.getNom())
                .ville(site.getVille())
                .adresse(site.getAdresse())
                .build();
    }

    /**
     * Convertit les données d'une requête en entité site.
     *
     * @param siteRequest Les données de la requête pour créer le site.
     * @return Site L'entité site créée.
     */
    public Site toEntity(SiteRequestDto siteRequest) {
        return Site.builder()
                .nom(siteRequest.getNom())
                .ville(siteRequest.getVille())
                .adresse(siteRequest.getAdresse())
                .build();
    }

    /**
     * Crée un nouveau site.
     *
     * @param siteRequest les informations du site à créer
     * @return le site créé
     */
    public SiteResponseDto createSite(SiteRequestDto siteRequest) {
        return toDto(siteRepository.save(toEntity(siteRequest)));
    }

    /**
     * Récupère un site à partir de son identifiant.
     *
     * @param id L'identifiant du site à récupérer.
     * @return SiteResponseDto Les détails du site récupéré.
     */
    public SiteResponseDto getSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site non trouvé"));
        return toDto(site);
    }

    /**
     * Récupère la liste de tous les sites.
     *
     * @return List<SiteResponseDto> La liste des sites récupérés.
     */
    public List<SiteResponseDto> getAllSites() {
        return siteRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Modifie un site existant.
     *
     * @param id L'identifiant du site à modifier.
     * @param siteRequest Les données de la requête pour modifier le site.
     * @return SiteResponseDto Les détails du site modifié.
     */
    public SiteResponseDto updateSiteById(Long id, SiteRequestDto siteRequest) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site non trouvé"));
        site.setNom(siteRequest.getNom());
        site.setVille(siteRequest.getVille());
        site.setAdresse(siteRequest.getAdresse());
        return toDto(siteRepository.save(site));
    }

    /**
     * Supprime un site à partir de son identifiant.
     *
     * @param id L'identifiant du site à supprimer.
     * @return SiteResponseDto Les détails du site supprimé.
     */
    public SiteResponseDto deleteSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site non trouvé"));
        siteRepository.deleteById(id);
        return toDto(site);
    }
}