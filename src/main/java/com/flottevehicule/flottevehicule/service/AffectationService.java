package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.RestitutionRequestDto;
import com.flottevehicule.flottevehicule.entity.Affectation;
import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.AffectationRepository;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;
import com.flottevehicule.flottevehicule.repository.SalarieRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final SalarieRepository salarieRepository;
    private final VehiculeRepository vehiculeRepository;
    private final EntretienRepository entretienRepository;
    private final SalarieService salarieService;
    private final VehiculeService vehiculeService;

    public AffectationResponseDto toDto(Affectation affectation) {
        return AffectationResponseDto.builder()
                .id(affectation.getId())
                .dateDebut(affectation.getDateDebut())
                .dateFin(affectation.getDateFin())
                .salarie(salarieService.toDto(affectation.getSalarie()))
                .vehicule(vehiculeService.toDto(affectation.getVehicule()))
                .build();
    }

    public Affectation toEntity(AffectationRequestDto affectationRequest, Salarie salarie, Vehicule vehicule) {
        return Affectation.builder()
                .dateDebut(affectationRequest.getDateDebut())
                .dateFin(affectationRequest.getDateFin())
                .salarie(salarie)
                .vehicule(vehicule)
                .build();
    }

    public AffectationResponseDto createAffectation(AffectationRequestDto affectationRequest) {
        Salarie salarie = salarieRepository.findById(affectationRequest.getSalarieId())
                .orElseThrow(() -> new NotFoundException("Salarié non trouvé"));
        Vehicule vehicule = vehiculeRepository.findById(affectationRequest.getVehiculeId())
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé"));

        LocalDateTime debut = affectationRequest.getDateDebut();
        LocalDateTime fin = affectationRequest.getDateFin();

        if (debut != null && vehicule.getDateMiseEnCirculation() != null &&
                debut.isBefore(vehicule.getDateMiseEnCirculation())) {
            throw new RequeteInvalideException("La date de début ne peut pas précéder la mise en service du véhicule");
        }

        verifierChevauchementAffectation(vehicule.getId(), debut, fin);

        verifierImmobilisation(vehicule.getId(), debut, fin);

        return toDto(affectationRepository.save(toEntity(affectationRequest, salarie, vehicule)));
    }

    private void verifierChevauchementAffectation(Long vehiculeId, LocalDateTime debut, LocalDateTime fin) {
        List<Affectation> existantes = affectationRepository.findByVehiculeIdOrderByDateDebutDesc(vehiculeId);
        for (Affectation a : existantes) {
            if (a.getDateFin() == null) {
                if (debut == null || !debut.isBefore(a.getDateDebut())) {
                    throw new RessourceUtiliseeException(
                            "Véhicule déjà affecté à " + a.getSalarie().getNom() + " depuis le " + a.getDateDebut());
                }
            } else if (debut != null && fin != null) {
                if (debut.isBefore(a.getDateFin()) && fin.isAfter(a.getDateDebut())) {
                    throw new RessourceUtiliseeException(
                            "Véhicule déjà affecté à " + a.getSalarie().getNom() +
                            " du " + a.getDateDebut() + " au " + a.getDateFin());
                }
            }
        }
    }

    private void verifierImmobilisation(Long vehiculeId, LocalDateTime debut, LocalDateTime fin) {
        List<Entretien> entretiens = entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(vehiculeId);
        for (Entretien e : entretiens) {
            LocalDateTime debutImmob = e.getDateIntervention();
            LocalDateTime finImmob = e.getDateIntervention().plusDays(e.getDureeImmobilisatin());
            if (debut != null && fin != null) {
                if (debut.isBefore(finImmob) && fin.isAfter(debutImmob)) {
                    throw new RessourceUtiliseeException(
                            "Véhicule immobilisé pour entretien du " + debutImmob + " au " + finImmob);
                }
            }
        }
    }

    public AffectationResponseDto getAffectationById(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affectation non trouvée avec l'id " + id));
        return toDto(affectation);
    }

    public List<AffectationResponseDto> getAllAffectations() {
        return affectationRepository.findAllByOrderByDateDebutDesc().stream()
                .map(this::toDto)
                .toList();
    }

    public AffectationResponseDto updateAffectationById(Long id, AffectationRequestDto affectationRequest) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affectation non trouvée avec l'id " + id));
        affectation.setDateFin(affectationRequest.getDateFin());
        return toDto(affectationRepository.save(affectation));
    }

    public AffectationResponseDto deleteAffectationById(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affectation non trouvée avec l'id " + id));
        if (affectation.getDateDebut() != null && affectation.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RessourceUtiliseeException("Impossible de supprimer une affectation déjà commencée, utilisez la restitution");
        }
        affectationRepository.deleteById(id);
        return toDto(affectation);
    }

    public List<AffectationResponseDto> getAffectationsBySalarie(Long salarieId) {
        return affectationRepository.findBySalarieIdOrderByDateDebutDesc(salarieId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<AffectationResponseDto> getAffectationsByVehicule(Long vehiculeId) {
        return affectationRepository.findByVehiculeIdOrderByDateDebutDesc(vehiculeId).stream()
                .map(this::toDto)
                .toList();
    }

    public AffectationResponseDto restituerAffectation(Long id, RestitutionRequestDto restitutionRequest) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affectation non trouvée avec l'id " + id));

        if (affectation.getDateFin() != null) {
            throw new RessourceUtiliseeException("Cette affectation est déjà clôturée");
        }

        LocalDateTime dateRestitution = restitutionRequest.getDateRestitution();
        if (dateRestitution.isBefore(affectation.getDateDebut())) {
            throw new RequeteInvalideException("La date de restitution doit être postérieure au début de l'affectation");
        }
        if (dateRestitution.isAfter(LocalDateTime.now())) {
            throw new RequeteInvalideException("La date de restitution ne peut pas être dans le futur");
        }

        Integer kmRestitution = restitutionRequest.getKilometrage();
        Vehicule vehicule = affectation.getVehicule();
        Integer dernierKm = vehicule.getKilometrageActuel();
        if (dernierKm != null && kmRestitution != null && kmRestitution < dernierKm) {
            throw new RequeteInvalideException(
                    "Le kilométrage doit être supérieur ou égal au dernier kilométrage connu (" + dernierKm + " km)");
        }

        affectation.setDateFin(dateRestitution);
        affectation.setKilometrageRestitution(kmRestitution);
        if (kmRestitution != null) {
            vehicule.setKilometrageActuel(kmRestitution);
            vehiculeRepository.save(vehicule);
        }

        return toDto(affectationRepository.save(affectation));
    }
}