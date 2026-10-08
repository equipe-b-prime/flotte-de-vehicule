package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.flottevehicule.flottevehicule.entity.Salarie;
import com.flottevehicule.flottevehicule.repository.SalarieRepository;

/**
 * Service métier gérant les salariés.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO.
 */
@Service
@RequiredArgsConstructor
public class SalarieService {

    private final SalarieRepository salarieRepository;

    /**
     * Convertit une entité salarié en objet de réponse.
     *
     * @param salarie L'entité salarié à convertir.
     * @return SalarieResponseDtotoVehiculeResponse Les détails du salarié converti.
     */
    public SalarieResponseDto toDto(Salarie salarie) {
        return SalarieResponseDto.builder()
                .id(salarie.getId())
                .nom(salarie.getNom())
                .prenom(salarie.getPrenom())
                .matricule(salarie.getMatricule())
                .service(salarie.getService())
                .build();
    }

    /**
     * Convertit les données d'une requête en entité salarié.
     *
     * @param salarieRequest Les données de la requête pour créer le salarié.
     * @return Salarie L'entité salarié créée.
     */
    public Salarie toEntity(SalarieRequestDto salarieRequest) {
        return Salarie.builder()
                .nom(salarieRequest.getNom())
                .prenom(salarieRequest.getPrenom())
                .matricule(salarieRequest.getMatricule())
                .service(salarieRequest.getService())
                .build();
    }

    /**
     * Crée un nouveau salarié.
     *
     * @param salarieRequest les informations du salarié à créer
     * @return le salarié créé
     */
    public SalarieResponseDto createSalarie(SalarieRequestDto salarieRequest) {
        return toDto(salarieRepository.save(toEntity(salarieRequest)));
    }

    /**
     * Récupère un salarié à partir de son identifiant.
     *
     * @param id L'identifiant du salarié à récupérer.
     * @return SalarieResponseDto Les détails du salarié récupéré.
     */
    public SalarieResponseDto getSalarieById(Long id) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salarié non trouvé"));
        return toDto(salarie);
    }

    /**
     * Récupère la liste de tous les salariés.
     *
     * @return List<SalarieResponseDto> La liste des salariés récupérés.
     */
    public List<SalarieResponseDto> getAllSalaries() {
        return salarieRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Modifie un salarié existant.
     *
     * @param id L'identifiant du salarié à modifier.
     * @param salarieRequest Les données de la requête pour modifier le salarié.
     * @return SalarieResponseDto Les détails du salarié modifié.
     */
    public SalarieResponseDto updateSalarieById(Long id, SalarieRequestDto salarieRequest) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salarié non trouvé"));
        salarie.setNom(salarieRequest.getNom());
        salarie.setPrenom(salarieRequest.getPrenom());
        salarie.setMatricule(salarieRequest.getMatricule());
        salarie.setService(salarieRequest.getService());
        return toDto(salarieRepository.save(salarie));
    }

    /**
     * Supprime un salarié à partir de son identifiant.
     *
     * @param id L'identifiant du salarié à supprimer.
     * @return SalarieResponseDto Les détails du salarié supprimé.
     */
    public SalarieResponseDto deleteSalarieById(Long id) {
        Salarie salarie = salarieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salarié non trouvé"));
        salarieRepository.deleteById(id);
        return toDto(salarie);
    }
}