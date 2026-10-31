package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.EtatAffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.RequestDto;
import com.flottevehicule.flottevehicule.dto.ResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculeRequestDto;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.entity.*;
import com.flottevehicule.flottevehicule.exception.DoublonRessourceException;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculeService extends AbstractCrudService {

    private final VehiculeRepository vehiculeRepository;
    private final SiteRepository siteRepository;
    private final ContratRepository contratRepository;
    private final ContratService contratService;
    private final SiteService siteService;
    private final AffectationRepository affectationRepository;
    private final EntretienRepository entretienRepository;
    private final EtatAffectationRepository etatAffectationRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                           SiteRepository siteRepository,
                           ContratRepository contratRepository,
                           ContratService contratService,
                           SiteService siteService,
                           AffectationRepository affectationRepository,
                           EntretienRepository entretienRepository,
                           EtatAffectationRepository etatAffectationRepository) {
        super(vehiculeRepository);
        this.vehiculeRepository = vehiculeRepository;
        this.siteRepository = siteRepository;
        this.contratRepository = contratRepository;
        this.contratService = contratService;
        this.siteService = siteService;
        this.affectationRepository = affectationRepository;
        this.entretienRepository = entretienRepository;
        this.etatAffectationRepository = etatAffectationRepository;
    }

    @Override
    public ResponseDto toDto(Object entity) {
        return toDto((Vehicule) entity);
    }

    @Override
    public ResponseDto create(RequestDto request) {
        return createVehicule((VehiculeRequestDto) request);
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        return updateVehiculeById(id, (VehiculeRequestDto) request);
    }

    public EtatAffectationResponseDto toDto(EtatAffectation etat) {
        if (etat == null) return null;
        return EtatAffectationResponseDto.builder()
                .id(etat.getId())
                .code(etat.getCode())
                .libelle(etat.getLibelle())
                .build();
    }

    public VehiculeResponseDto toDto(Vehicule vehicule) {
        EtatAffectation etat = vehicule.getEtatAffectation();
        if (etat == null && vehicule.getId() != null) {
            LocalDateTime now = LocalDateTime.now();
            boolean estAffecte = affectationRepository.findByVehiculeIdOrderByDateDebutDesc(vehicule.getId()).stream()
                    .anyMatch(a -> a.getDateDebut() != null && !a.getDateDebut().isAfter(now)
                            && (a.getDateFin() == null || a.getDateFin().isAfter(now)));
            String code = estAffecte ? "AFFECTE" : "DISPONIBLE";
            etat = etatAffectationRepository.findByCode(code)
                    .orElseGet(() -> etatAffectationRepository.save(EtatAffectation.builder()
                            .code(code)
                            .libelle(estAffecte ? "Affecté" : "Disponible")
                            .build()));
        }

        return VehiculeResponseDto.builder()
                .id(vehicule.getId())
                .nom(vehicule.getNom())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .dateMiseEnCirculation(vehicule.getDateMiseEnCirculation())
                .site(siteService.toDto(vehicule.getSite()))
                .contrat(contratService.toDto(vehicule.getContrat()))
                .etatAffectation(toDto(etat))
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
        Contrat contrat = null;
        if (vehiculeRequest.getContratId() != null) {
            contrat = contratRepository.findById(vehiculeRequest.getContratId())
                    .orElseThrow(() -> new NotFoundException("Contrat non trouvé"));
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
                .orElseThrow(() -> new NotFoundException("Site non trouvé"));
        vehicule.setSite(site);
        return toDto(vehiculeRepository.save(vehicule));
    }

    public VehiculeResponseDto deleteVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + id));
        boolean aAffectations = !affectationRepository.findByVehiculeIdOrderByDateDebutDesc(id).isEmpty();
        boolean aEntretiens = !entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(id).isEmpty();
        if (aAffectations || aEntretiens) {
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
