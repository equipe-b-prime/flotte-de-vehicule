package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.RequestDto;
import com.flottevehicule.flottevehicule.dto.ResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteRequestDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.exception.DoublonRessourceException;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.SiteRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService extends AbstractCrudService {

    private final SiteRepository siteRepository;
    private final VehiculeRepository vehiculeRepository;

    public SiteService(SiteRepository siteRepository, VehiculeRepository vehiculeRepository) {
        super(siteRepository);
        this.siteRepository = siteRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public ResponseDto toDto(Object entity) {
        return toDto((Site) entity);
    }

    @Override
    public ResponseDto create(RequestDto request) {
        return createSite((SiteRequestDto) request);
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        return updateSiteById(id, (SiteRequestDto) request);
    }

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
