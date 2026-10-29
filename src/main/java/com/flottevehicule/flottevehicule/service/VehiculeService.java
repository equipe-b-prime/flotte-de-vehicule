package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.exception.DoublonRessourceException;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final SiteRepository siteRepository;
    private final ContratRepository contratRepository;
    private final AffectationRepository affectationRepository;
    private final EntretienRepository entretienRepository;
    private final SiteService siteService;
    private final ContratService contratService;

    public VehiculeResponseDto toDto(Vehicule vehicule) {
        return VehiculeResponseDto.builder()
                .id(vehicule.getId())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .dateMiseEnCirculation(vehicule.getDateMiseEnCirculation())
                .kilometrageActuel(vehicule.getKilometrageActuel())
                .site(siteService.toDto(vehicule.getSite()))
                .contrat(contratService.toDto(vehicule.getContrat()))
                .build();
    }

    public Vehicule toEntity(VehiculeRequestDto vehiculeRequest, Site site, Contrat contrat) {
        return Vehicule.builder()
                .immatriculation(vehiculeRequest.getImmatriculation())
                .marque(vehiculeRequest.getMarque())
                .modele(vehiculeRequest.getModele())
                .dateMiseEnCirculation(vehiculeRequest.getDateMiseEnCirculation())
                .site(site)
                .contrat(contrat)
                .build();
    }

    public VehiculeResponseDto createVehicule(VehiculeRequestDto vehiculeRequest) {
        if (vehiculeRepository.existsByImmatriculation(vehiculeRequest.getImmatriculation())) {
            throw new DoublonRessourceException("Un véhicule existe déjà avec cette immatriculation");
        }

        if (vehiculeRequest.getDateMiseEnCirculation() != null &&
                vehiculeRequest.getDateMiseEnCirculation().isAfter(LocalDateTime.now())) {
            throw new RequeteInvalideException("La date de mise en circulation ne peut pas être dans le futur");
        }

        Site site = siteRepository.findById(vehiculeRequest.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + vehiculeRequest.getSiteId()));

        Contrat contrat = null;
        if (vehiculeRequest.getContratId() != null) {
            contrat = contratRepository.findById(vehiculeRequest.getContratId())
                    .orElseThrow(() -> new NotFoundException("Contrat non trouvé avec l'id " + vehiculeRequest.getContratId()));
        }

        return toDto(vehiculeRepository.save(toEntity(vehiculeRequest, site, contrat)));
    }

    public VehiculeResponseDto getVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + id));
        return toDto(vehicule);
    }

    public List<VehiculeResponseDto> getAllVehicules() {
        return vehiculeRepository.findAllByOrderByImmatriculationAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public VehiculeResponseDto updateVehiculeById(Long id, VehiculeRequestDto vehiculeRequest) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + id));

        Site site = siteRepository.findById(vehiculeRequest.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + vehiculeRequest.getSiteId()));

        vehicule.setSite(site);
        return toDto(vehiculeRepository.save(vehicule));
    }

    public VehiculeResponseDto deleteVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + id));

        if (!affectationRepository.findByVehiculeIdOrderByDateDebutDesc(id).isEmpty()) {
            throw new RessourceUtiliseeException("Le véhicule a des affectations");
        }

        if (!entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(id).isEmpty()) {
            throw new RessourceUtiliseeException("Le véhicule a des entretiens");
        }

        vehiculeRepository.deleteById(id);
        return toDto(vehicule);
    }

    public List<VehiculeResponseDto> getVehiculesBySite(Long siteId) {
        siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + siteId));
        return vehiculeRepository.findBySiteIdOrderByImmatriculationAsc(siteId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<VehiculeResponseDto> searchVehicules(String immatriculation, String marque) {
        if (immatriculation != null && !immatriculation.isBlank() && marque != null && !marque.isBlank()) {
            return vehiculeRepository.findByImmatriculationContainingIgnoreCaseAndMarqueContainingIgnoreCase(immatriculation, marque).stream()
                    .map(this::toDto)
                    .toList();
        } else if (immatriculation != null && !immatriculation.isBlank()) {
            return vehiculeRepository.findByImmatriculationContainingIgnoreCase(immatriculation).stream()
                    .map(this::toDto)
                    .toList();
        } else if (marque != null && !marque.isBlank()) {
            return vehiculeRepository.findByMarqueContainingIgnoreCase(marque).stream()
                    .map(this::toDto)
                    .toList();
        }
        return getAllVehicules();
    }

    public ProchainEntretienResponseDto calculerProchainEntretien(Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + vehiculeId));

        Optional<Entretien> dernierEntretien = entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(vehiculeId)
                .stream().findFirst();

        LocalDateTime pointDepart;
        int kilometrageDepart;

        if (dernierEntretien.isPresent() && dernierEntretien.get().getTypeEntretien() != null
                && Boolean.TRUE.equals(dernierEntretien.get().getTypeEntretien().getEstRegulier())) {
            pointDepart = dernierEntretien.get().getDateIntervention();
            kilometrageDepart = dernierEntretien.get().getKilometrage() != null ?
                    dernierEntretien.get().getKilometrage() : 0;
        } else {
            pointDepart = vehicule.getDateMiseEnCirculation() != null ? vehicule.getDateMiseEnCirculation() : LocalDateTime.now();
            kilometrageDepart = 0;
        }

        LocalDateTime dateEcheance = pointDepart.plusYears(1);
        int kilometrageCible = kilometrageDepart + 15000;

        String seuil;
        if (dateEcheance.isBefore(LocalDateTime.now().plusMonths(12))) {
            seuil = "date";
        } else {
            seuil = "kilométrage";
        }

        return ProchainEntretienResponseDto.builder()
                .dateEstimee(dateEcheance)
                .kilometrageCible(kilometrageCible)
                .seuilDeclencheur(seuil)
                .build();
    }
}
