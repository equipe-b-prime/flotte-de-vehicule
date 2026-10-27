package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.exception.DoublonRessourceException;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.SiteRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final VehiculeRepository vehiculeRepository;

    public SiteResponseDto toDto(Site site) {
        return SiteResponseDto.builder()
                .id(site.getId())
                .nom(site.getNom())
                .ville(site.getVille())
                .adresse(site.getAdresse())
                .build();
    }

    public Site toEntity(SiteRequestDto siteRequest) {
        return Site.builder()
                .nom(siteRequest.getNom())
                .ville(siteRequest.getVille())
                .adresse(siteRequest.getAdresse())
                .build();
    }

    public SiteResponseDto createSite(SiteRequestDto siteRequest) {
        if (siteRepository.findByNom(siteRequest.getNom()).isPresent()) {
            throw new DoublonRessourceException("Un site avec le nom '" + siteRequest.getNom() + "' existe déjà");
        }
        return toDto(siteRepository.save(toEntity(siteRequest)));
    }

    public SiteResponseDto getSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + id));
        return toDto(site);
    }

    public List<SiteResponseDto> getAllSites() {
        return siteRepository.findAllByOrderByVilleAscNomAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public SiteResponseDto updateSiteById(Long id, SiteRequestDto siteRequest) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + id));
        site.setAdresse(siteRequest.getAdresse());
        site.setVille(siteRequest.getVille());
        return toDto(siteRepository.save(site));
    }

    public SiteResponseDto deleteSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + id));
        if (vehiculeRepository.existsBySiteId(id)) {
            throw new RessourceUtiliseeException("Impossible de supprimer le site : il contient des véhicules");
        }
        siteRepository.deleteById(id);
        return toDto(site);
    }
}