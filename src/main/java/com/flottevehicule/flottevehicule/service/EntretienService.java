package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.entity.Prestataire;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;
import com.flottevehicule.flottevehicule.repository.PrestataireRepository;
import com.flottevehicule.flottevehicule.repository.TypeEntretienRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntretienService {

    private final EntretienRepository entretienRepository;
    private final VehiculeRepository vehiculeRepository;
    private final TypeEntretienRepository typeEntretienRepository;
    private final PrestataireRepository prestataireRepository;
    private final SiteService siteService;
    private final ContratService contratService;
    private final PrestataireService prestataireService;
    private final TypeEntretienService typeEntretienService;
    private final VehiculeService vehiculeService;

    public EntretienResponseDto toDto(Entretien entretien) {
        return EntretienResponseDto.builder()
                .id(entretien.getId())
                .dateIntervention(entretien.getDateIntervention())
                .cout(entretien.getCout())
                .dureeImmobilisatin(entretien.getDureeImmobilisatin())
                .kilometrage(entretien.getKilometrage())
                .vehicule(vehiculeService.toDto(entretien.getVehicule()))
                .typeEntretien(typeEntretienService.toDto(entretien.getTypeEntretien()))
                .prestataire(prestataireService.toDto(entretien.getPrestataire()))
                .build();
    }

    public Entretien toEntity(EntretienRequestDto entretienRequest, Vehicule vehicule,
                              TypeEntretien typeEntretien, Prestataire prestataire) {
        return Entretien.builder()
                .dateIntervention(entretienRequest.getDateIntervention())
                .cout(entretienRequest.getCout())
                .dureeImmobilisatin(entretienRequest.getDureeImmobilisatin())
                .kilometrage(entretienRequest.getKilometrage())
                .vehicule(vehicule)
                .typeEntretien(typeEntretien)
                .prestataire(prestataire)
                .build();
    }

    public EntretienResponseDto createEntretien(EntretienRequestDto entretienRequest) {
        Vehicule vehicule = vehiculeRepository.findById(entretienRequest.getVehiculeId())
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé"));
        TypeEntretien typeEntretien = typeEntretienRepository.findById(entretienRequest.getTypeEntretienId())
                .orElseThrow(() -> new NotFoundException("Type d'entretien non trouvé"));
        Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                .orElseThrow(() -> new NotFoundException("Prestataire non trouvé"));

        if (entretienRequest.getCout() == null || entretienRequest.getCout().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequeteInvalideException("Le coût doit être positif");
        }

        if (entretienRequest.getDateIntervention() != null && vehicule.getDateMiseEnCirculation() != null &&
                entretienRequest.getDateIntervention().isBefore(vehicule.getDateMiseEnCirculation())) {
            throw new RequeteInvalideException("La date d'entretien ne peut pas précéder la mise en service du véhicule");
        }

        Integer dernierKm = entretienRepository.findDernierKilometrage(vehicule.getId());
        if (dernierKm != null && entretienRequest.getKilometrage() != null &&
                entretienRequest.getKilometrage() <= dernierKm) {
            throw new RequeteInvalideException(
                    "Le kilométrage doit être supérieur au dernier kilométrage connu (" + dernierKm + " km)");
        }

        return toDto(entretienRepository.save(toEntity(entretienRequest, vehicule, typeEntretien, prestataire)));
    }

    public EntretienResponseDto getEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        return toDto(entretien);
    }

    public List<EntretienResponseDto> getAllEntretiens() {
        return entretienRepository.findAllByOrderByDateInterventionDesc().stream()
                .map(this::toDto)
                .toList();
    }

    public EntretienResponseDto updateEntretienById(Long id, EntretienRequestDto entretienRequest) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        entretien.setCout(entretienRequest.getCout());
        if (entretienRequest.getPrestataireId() != null) {
            Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                    .orElseThrow(() -> new NotFoundException("Prestataire non trouvé"));
            entretien.setPrestataire(prestataire);
        }
        return toDto(entretienRepository.save(entretien));
    }

    public EntretienResponseDto deleteEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        entretienRepository.deleteById(id);
        return toDto(entretien);
    }
}