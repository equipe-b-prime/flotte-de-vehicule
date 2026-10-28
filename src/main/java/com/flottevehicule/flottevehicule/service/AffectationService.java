package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.entity.Affectation;
import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.AffectationRepository;
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
        if (debut != null && vehicule.getDateMiseEnCirculation() != null &&
                debut.isBefore(vehicule.getDateMiseEnCirculation())) {
            throw new RequeteInvalideException("La date de début ne peut pas précéder la mise en service du véhicule");
        }

        return toDto(affectationRepository.save(toEntity(affectationRequest, salarie, vehicule)));
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
        salarieRepository.findById(salarieId)
                .orElseThrow(() -> new NotFoundException("Salarié non trouvé avec l'id " + salarieId));
        return affectationRepository.findBySalarieIdOrderByDateDebutDesc(salarieId).stream()
                .map(this::toDto)
                .toList();
    }
}
