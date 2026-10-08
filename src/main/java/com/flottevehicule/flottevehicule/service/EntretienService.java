package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.ContratResponseDto;
import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.entity.Prestataire;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;
import com.flottevehicule.flottevehicule.repository.PrestataireRepository;
import com.flottevehicule.flottevehicule.dto.PrestataireResponseDto;
import com.flottevehicule.flottevehicule.dto.SiteResponseDto;
import com.flottevehicule.flottevehicule.repository.TypeEntretienRepository;
import com.flottevehicule.flottevehicule.dto.TypeEntretienResponseDto;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import com.flottevehicule.flottevehicule.dto.VehiculeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;

/**
 * Service métier gérant les entretiens.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO,
 * avec résolution des relations véhicule, type d'entretien et prestataire.
 */
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


    /**
     * Convertit une entité entretien en objet de réponse.
     *
     * @param entretien L'entité entretien à convertir.
     * @return EntretienResponseDto Les détails de l'entretien converti.
     */
    public EntretienResponseDto toDto(Entretien entretien) {
        return EntretienResponseDto.builder()
                .id(entretien.getId())
                .dateIntervention(entretien.getDateIntervention())
                .cout(entretien.getCout())
                .dureeImmobilisatin(entretien.getDureeImmobilisatin())
                .vehicule(vehiculeService.toDto(entretien.getVehicule()))
                .typeEntretien(typeEntretienService.toDto(entretien.getTypeEntretien()))
                .prestataire(prestataireService.toDto(entretien.getPrestataire()))
                .build();
    }

    /**
     * Convertit les données d'une requête en entité entretien.
     *
     * @param entretienRequest Les données de la requête pour créer l'entretien.
     * @param vehicule Le véhicule concerné.
     * @param typeEntretien Le type d'entretien.
     * @param prestataire Le prestataire réalisant l'entretien.
     * @return Entretien L'entité entretien créée.
     */
    public Entretien toEntity(EntretienRequestDto entretienRequest, Vehicule vehicule,
                              TypeEntretien typeEntretien, Prestataire prestataire) {
        return Entretien.builder()
                .dateIntervention(entretienRequest.getDateIntervention())
                .cout(entretienRequest.getCout())
                .dureeImmobilisatin(entretienRequest.getDureeImmobilisatin())
                .vehicule(vehicule)
                .typeEntretien(typeEntretien)
                .prestataire(prestataire)
                .build();
    }

    /**
     * Crée un nouvel entretien.
     *
     * @param entretienRequest les informations de l'entretien à créer
     * @return l'entretien créé
     */
    public EntretienResponseDto createEntretien(EntretienRequestDto entretienRequest) {
        Vehicule vehicule = vehiculeRepository.findById(entretienRequest.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        TypeEntretien typeEntretien = typeEntretienRepository.findById(entretienRequest.getTypeEntretienId())
                .orElseThrow(() -> new RuntimeException("Type d'entretien non trouvé"));
        Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));
        return toDto(entretienRepository.save(toEntity(entretienRequest, vehicule, typeEntretien, prestataire)));
    }

    /**
     * Récupère un entretien à partir de son identifiant.
     *          .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));
        affectationRepository.deleteById(id);
        return toDto(affectation);
     * @param id L'identifiant de l'entretien à récupérer.
     * @return EntretienResponseDto Les détails de l'entretien récupéré.
     */
    public EntretienResponseDto getEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
        return toDto(entretien);
    }

    /**
     * Récupère la liste de tous les entretiens.
     *
     * @return List<EntretienResponseDto> La liste des entretiens récupérés.
     */
    public List<EntretienResponseDto> getAllEntretiens() {
        return entretienRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Modifie un entretien existant.
     *
     * @param id L'identifiant de l'entretien à modifier.
     * @param entretienRequest Les données de la requête pour modifier l'entretien.
     * @return EntretienResponseDto Les détails de l'entretien modifié.
     */
    public EntretienResponseDto updateEntretienById(Long id, EntretienRequestDto entretienRequest) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
        Vehicule vehicule = vehiculeRepository.findById(entretienRequest.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        TypeEntretien typeEntretien = typeEntretienRepository.findById(entretienRequest.getTypeEntretienId())
                .orElseThrow(() -> new RuntimeException("Type d'entretien non trouvé"));
        Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));
        entretien.setDateIntervention(entretienRequest.getDateIntervention());
        entretien.setCout(entretienRequest.getCout());
        entretien.setDureeImmobilisatin(entretienRequest.getDureeImmobilisatin());
        entretien.setVehicule(vehicule);
        entretien.setTypeEntretien(typeEntretien);
        entretien.setPrestataire(prestataire);
        return toDto(entretienRepository.save(entretien));
    }

    /**
     * Supprime un entretien à partir de son identifiant.
     *
     * @param id L'identifiant de l'entretien à supprimer.
     * @return EntretienResponseDto Les détails de l'entretien supprimé.
     */
    public EntretienResponseDto deleteEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
        entretienRepository.deleteById(id);
        return toDto(entretien);
    }



}