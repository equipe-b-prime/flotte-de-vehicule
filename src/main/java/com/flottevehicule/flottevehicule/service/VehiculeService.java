package com.flottevehicule.flottevehicule.service;

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
import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.repository.AffectationRepository;
import com.flottevehicule.flottevehicule.repository.ContratRepository;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;
import com.flottevehicule.flottevehicule.repository.SiteRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final SiteRepository siteRepository;
    private final ContratRepository contratRepository;
    private final ContratService contratService;
    private final SiteService siteService;
    private final AffectationRepository affectationRepository;
    private final EntretienRepository entretienRepository;

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

    public VehiculeResponseDto createVehicule(VehiculeRequestDto vehiculeRequest) {
        if (vehiculeRepository.findByImmatriculation(vehiculeRequest.getImmatriculation()).isPresent()) {
            throw new DoublonRessourceException("Un véhicule avec l'immatriculation '" + vehiculeRequest.getImmatriculation() + "' existe déjà");
        }
        if (vehiculeRequest.getDateMiseEnCirculation() != null &&
                vehiculeRequest.getDateMiseEnCirculation().toLocalDate().isAfter(LocalDate.now())) {
            throw new RequeteInvalideException("La date de mise en service ne peut pas être dans le futur");
        }
        Site site = siteRepository.findById(vehiculeRequest.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site non trouvé"));
        Contrat contrat = contratRepository.findById(vehiculeRequest.getContratId())
                .orElseThrow(() -> new NotFoundException("Contrat non trouvé"));
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
                .orElseThrow(() -> new NotFoundException("Site non trouvé"));
        vehicule.setSite(site);
        return toDto(vehiculeRepository.save(vehicule));
    }

    public VehiculeResponseDto deleteVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + id));
        boolean aAffectations = affectationRepository.findByVehiculeIdOrderByDateDebutDesc(id).isEmpty();
        boolean aEntretiens = entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(id).isEmpty();
        if (!aAffectations || !aEntretiens) {
            throw new RessourceUtiliseeException("Impossible de supprimer le véhicule : il a des affectations ou des entretiens");
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
        if (immatriculation != null && !immatriculation.isEmpty() && marque != null && !marque.isEmpty()) {
            return vehiculeRepository.findByImmatriculationContainingIgnoreCaseAndMarqueContainingIgnoreCase(immatriculation, marque).stream()
                    .map(this::toDto)
                    .toList();
        } else if (immatriculation != null && !immatriculation.isEmpty()) {
            return vehiculeRepository.findByImmatriculationContainingIgnoreCase(immatriculation).stream()
                    .map(this::toDto)
                    .toList();
        } else if (marque != null && !marque.isEmpty()) {
            return vehiculeRepository.findByMarqueContainingIgnoreCase(marque).stream()
                    .map(this::toDto)
                    .toList();
        }
        return getAllVehicules();
    }

    public ProchainEntretienResponseDto calculerProchainEntretien(Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + vehiculeId));

        List<Entretien> entretiens = entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(vehiculeId).stream().toList();

        LocalDateTime dernierEntretien = null;
        if (!entretiens.isEmpty()) {
            dernierEntretien = entretiens.get(0).getDateIntervention();
        }

        LocalDateTime dateEstimee;
        Integer kilometrageCible;
        String seuilDeclencheur;

        if (dernierEntretien == null) {
            if (vehicule.getDateMiseEnCirculation() != null) {
                dateEstimee = vehicule.getDateMiseEnCirculation();
            } else {
                dateEstimee = LocalDateTime.now();
            }
            kilometrageCible = 0;
            seuilDeclencheur = "mise en service";
        } else {
            LocalDateTime unAnApres = dernierEntretien.plusYears(1);
            dateEstimee = unAnApres;
            if (entretiens.get(0).getKilometrage() != null) {
                kilometrageCible = entretiens.get(0).getKilometrage() + 15000;
            } else {
                kilometrageCible = 0;
            }
            seuilDeclencheur = "15 000 km ou 1 an";
        }

        return ProchainEntretienResponseDto.builder()
                .dateEstimee(dateEstimee)
                .kilometrageCible(kilometrageCible)
                .seuilDeclencheur(seuilDeclencheur)
                .build();
    }
}
