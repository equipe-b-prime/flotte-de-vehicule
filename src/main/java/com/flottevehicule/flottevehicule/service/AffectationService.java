package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.ContratResponseDto;
import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.repository.SalarieRepository;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.flottevehicule.flottevehicule.entity.Affectation;
import com.flottevehicule.flottevehicule.repository.AffectationRepository;

/**
 * Service métier gérant les affectations.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO,
 * avec résolution des relations salarié et véhicule par identifiant.
 */
@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final SalarieRepository salarieRepository;
    private final VehiculeRepository vehiculeRepository;
    private final SalarieService salarieService;
    private final SiteService siteService;
    private final VehiculeService vehiculeService;
    /**
     * Convertit une entité affectation en objet de réponse.
     *
     * @param affectation L'entité affectation à convertir.
     * @return AffectationResponseDto Les détails de l'affectation convertie.
     */
    public AffectationResponseDto toDto(Affectation affectation) {
        return AffectationResponseDto.builder()
                .id(affectation.getId())
                .dateDebut(affectation.getDateDebut())
                .dateFin(affectation.getDateFin())
                .salarie(salarieService.toDto(affectation.getSalarie()))
                .vehicule(vehiculeService.toDto(affectation.getVehicule()))
                .build();
    }

    /**
     * Convertit les données d'une requête en entité affectation.
     *
     * @param affectationRequest Les données de la requête pour créer l'affectation.
     * @param salarie Le salarié affecté.
     * @param vehicule Le véhicule affecté.
     * @return Affectation L'entité affectation créée.
     */
    public Affectation toEntity(AffectationRequestDto affectationRequest, Salarie salarie, Vehicule vehicule) {
        return Affectation.builder()
                .dateDebut(affectationRequest.getDateDebut())
                .dateFin(affectationRequest.getDateFin())
                .salarie(salarie)
                .vehicule(vehicule)
                .build();
    }

    /**
     * Crée une nouvelle affectation.
     *
     * @param affectationRequest les informations de l'affectation à créer
     * @return l'affectation créée
     */
    public AffectationResponseDto createAffectation(AffectationRequestDto affectationRequest) {
        Salarie salarie = salarieRepository.findById(affectationRequest.getSalarieId())
                .orElseThrow(() -> new RuntimeException("Salarié non trouvé"));
        Vehicule vehicule = vehiculeRepository.findById(affectationRequest.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        return toDto(affectationRepository.save(toEntity(affectationRequest, salarie, vehicule)));
    }

    /**
     * Récupère une affectation à partir de son identifiant.
     *
     * @param id L'identifiant de l'affectation à récupérer.
     * @return AffectationResponseDto Les détails de l'affectation récupérée.
     */
    public AffectationResponseDto getAffectationById(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));
        return toDto(affectation);
    }

    /**
     * Récupère la liste de toutes les affectations.
     *
     * @return List<AffectationResponseDto> La liste des affectations récupérées.
     */
    public List<AffectationResponseDto> getAllAffectations() {
        return affectationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Modifie une affectation existante.
     *
     * @param id L'identifiant de l'affectation à modifier.
     * @param affectationRequest Les données de la requête pour modifier l'affectation.
     * @return AffectationResponseDto Les détails de l'affectation modifiée.
     */
    public AffectationResponseDto updateAffectationById(Long id, AffectationRequestDto affectationRequest) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));
        Salarie salarie = salarieRepository.findById(affectationRequest.getSalarieId())
                .orElseThrow(() -> new RuntimeException("Salarié non trouvé"));
        Vehicule vehicule = vehiculeRepository.findById(affectationRequest.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        affectation.setDateDebut(affectationRequest.getDateDebut());
        affectation.setDateFin(affectationRequest.getDateFin());
        affectation.setSalarie(salarie);
        affectation.setVehicule(vehicule);
        return toDto(affectationRepository.save(affectation));
    }

    /**
     * Supprime une affectation à partir de son identifiant.
     *
     * @param id L'identifiant de l'affectation à supprimer.
     * @return AffectationResponseDto Les détails de l'affectation supprimée.
     */
    public AffectationResponseDto deleteAffectationById(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));
        affectationRepository.deleteById(id);
        return toDto(affectation);
    }


}