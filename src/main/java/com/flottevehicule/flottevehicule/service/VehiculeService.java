package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.repository.ContratRepository;
import com.flottevehicule.flottevehicule.dto.ContratResponseDto;
import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.repository.SiteRepository;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;

/**
 * Service métier gérant les véhicules.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO,
 * avec résolution des relations site et contrat par identifiant.
 */
@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final SiteRepository siteRepository;
    private final ContratRepository contratRepository;
    private final ContratService contratService;
    private final SiteService siteService;
    /**
     * Convertit une entité véhicule en objet de réponse.
     *
     * @param vehicule L'entité véhicule à convertir.
     * @return VehiculeResponseDto Les détails du véhicule converti.
     */
    public VehiculeResponseDto toDto(Vehicule vehicule) {
        return VehiculeResponseDto.builder()
                .id(vehicule.getId())
                .nom(vehicule.getNom())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .dateMiseEnCirculation(vehicule.getDateMiseEnCirculation())
                .site(siteService.toDto(vehicule.getSite()))
                .contrat(contratService.toDto(vehicule.getContrat()))
                .build();
    }

    /**
     * Convertit les données d'une requête en entité véhicule.
     *
     * @param vehiculeRequest Les données de la requête pour créer le véhicule.
     * @param site Le site de rattachement.
     * @param contrat Le contrat associé.
     * @return Vehicule L'entité véhicule créée.
     */
    public Vehicule toEntity(VehiculeRequestDto vehiculeRequest, Site site, Contrat contrat) {
        return Vehicule.builder()
                .nom(vehiculeRequest.getNom())
                .immatriculation(vehiculeRequest.getImmatriculation())
                .marque(vehiculeRequest.getMarque())
                .modele(vehiculeRequest.getModele())
                .dateMiseEnCirculation(vehiculeRequest.getDateMiseEnCirculation())
                .site(site)
                .contrat(contrat)
                .build();
    }

    /**
     * Crée un nouveau véhicule.
     *
     * @param vehiculeRequest les informations du véhicule à créer
     * @return le véhicule créé
     */
    public VehiculeResponseDto createVehicule(VehiculeRequestDto vehiculeRequest) {
        Site site = siteRepository.findById(vehiculeRequest.getSiteId())
                .orElseThrow(() -> new RuntimeException("Site non trouvé"));
        Contrat contrat = contratRepository.findById(vehiculeRequest.getContratId())
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        return toDto(vehiculeRepository.save(toEntity(vehiculeRequest, site, contrat)));
    }

    /**
     * Récupère un véhicule à partir de son identifiant.
     *
     * @param id L'identifiant du véhicule à récupérer.
     * @return VehiculeResponseDto Les détails du véhicule récupéré.
     */
    public VehiculeResponseDto getVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        return toDto(vehicule);
    }

    /**    private final ContratService contratService;

     * Récupère la liste de tous les véhicules.
     *
     * @return List<VehiculeResponseDto> La liste des véhicules récupérés.
     */
    public List<VehiculeResponseDto> getAllVehicules() {
        return vehiculeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Modifie un véhicule existant.
     *
     * @param id L'identifiant du véhicule à modifier.
     * @param vehiculeRequest Les données de la requête pour modifier le véhicule.
     * @return VehiculeResponseDto Les détails du véhicule modifié.
     */
    public VehiculeResponseDto updateVehiculeById(Long id, VehiculeRequestDto vehiculeRequest) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        Site site = siteRepository.findById(vehiculeRequest.getSiteId())
                .orElseThrow(() -> new RuntimeException("Site non trouvé"));
        Contrat contrat = contratRepository.findById(vehiculeRequest.getContratId())
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        vehicule.setNom(vehiculeRequest.getNom());
        vehicule.setImmatriculation(vehiculeRequest.getImmatriculation());
        vehicule.setMarque(vehiculeRequest.getMarque());
        vehicule.setModele(vehiculeRequest.getModele());
        vehicule.setDateMiseEnCirculation(vehiculeRequest.getDateMiseEnCirculation());
        vehicule.setSite(site);
        vehicule.setContrat(contrat);
        return toDto(vehiculeRepository.save(vehicule));
    }

    /**
     * Supprime un véhicule à partir de son identifiant.
     *
     * @param id L'identifiant du véhicule à supprimer.
     * @return VehiculeResponseDto Les détails du véhicule supprimé.
     */
    public VehiculeResponseDto deleteVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        vehiculeRepository.deleteById(id);
        return toDto(vehicule);
    }

}