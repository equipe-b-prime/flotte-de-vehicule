package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.exception.DoublonRessourceException;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RessourceUtiliseeException;
import com.flottevehicule.flottevehicule.repository.AffectationRepository;
import com.flottevehicule.flottevehicule.repository.SalarieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalarieService {

    private final SalarieRepository salarieRepository;
    private final AffectationRepository affectationRepository;

    public SalarieResponseDto toDto(Salarie salarie) {
        return SalarieResponseDto.builder()
                .id(salarie.getId())
                .nom(salarie.getNom())
                .prenom(salarie.getPrenom())
                .matricule(salarie.getMatricule())
                .service(salarie.getService())
                .build();
    }

    public Salarie toEntity(SalarieRequestDto salarieRequest) {
        return Salarie.builder()
                .nom(salarieRequest.getNom())
                .prenom(salarieRequest.getPrenom())
                .matricule(salarieRequest.getMatricule())
                .service(salarieRequest.getService())
                .build();
    }

    public SalarieResponseDto createSalarie(SalarieRequestDto salarieRequest) {
        if (salarieRepository.findByMatricule(salarieRequest.getMatricule()).isPresent()) {
            throw new DoublonRessourceException("Un salarié avec le matricule '" + salarieRequest.getMatricule() + "' existe déjà");
        }
        return toDto(salarieRepository.save(toEntity(salarieRequest)));
    }

    public SalarieResponseDto getSalarieById(Long id) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salarié non trouvé avec l'id " + id));
        return toDto(salarie);
    }

    public List<SalarieResponseDto> getAllSalaries() {
        return salarieRepository.findAllByOrderByNomAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public SalarieResponseDto updateSalarieById(Long id, SalarieRequestDto salarieRequest) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salarié non trouvé avec l'id " + id));
        salarie.setService(salarieRequest.getService());
        return toDto(salarieRepository.save(salarie));
    }

    public SalarieResponseDto deleteSalarieById(Long id) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salarié non trouvé avec l'id " + id));
        boolean aAffectationEnCours = affectationRepository.findBySalarieIdOrderByDateDebutDesc(id)
                .stream().anyMatch(a -> a.getDateFin() == null);
        if (aAffectationEnCours) {
            throw new RessourceUtiliseeException("Impossible de supprimer le salarié : il a une affectation en cours");
        }
        salarieRepository.deleteById(id);
        return toDto(salarie);
    }
}