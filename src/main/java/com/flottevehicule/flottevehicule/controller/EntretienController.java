package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.EntretienService;

/**
 * Contrôleur REST exposant les endpoints de gestion des entretiens.
 */
@RestController
@RequestMapping("/entretiens")
@RequiredArgsConstructor
public class EntretienController {
    private final EntretienService entretienService;

    /**
     * Crée un nouvel entretien.
     *
     * @param entretienRequest les informations de l'entretien à créer
     * @return l'entretien créé
     */
    @PostMapping
    public EntretienResponseDto createEntretien(@RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.createEntretien(entretienRequest);
    }

    /**
     * Récupère un entretien à partir de son identifiant.
     *
     * @param id L'identifiant de l'entretien à récupérer.
     * @return EntretienResponseDto Les détails de l'entretien récupéré.
     */
    @GetMapping("/{id}")
    public EntretienResponseDto getEntretienById(@PathVariable Long id) {
        return entretienService.getEntretienById(id);
    }

    /**
     * Récupère la liste de tous les entretiens.
     *
     * @return List<EntretienResponseDto> La liste des entretiens récupérés.
     */
    @GetMapping
    public List<EntretienResponseDto> getAllEntretiens() {
        return entretienService.getAllEntretiens();
    }

    /**
     * Modifie un entretien existant.
     *
     * @param id L'identifiant de l'entretien à modifier.
     * @param entretienRequest Les données de la requête pour modifier l'entretien.
     * @return EntretienResponseDto Les détails de l'entretien modifié.
     */
    @PutMapping("/{id}")
    public EntretienResponseDto updateEntretienById(@PathVariable Long id, @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.updateEntretienById(id, entretienRequest);
    }

    /**
     * Supprime un entretien à partir de son identifiant.
     *
     * @param id L'identifiant de l'entretien à supprimer.
     * @return EntretienResponseDto Les détails de l'entretien supprimé.
     */
    @DeleteMapping("/{id}")
    public EntretienResponseDto deleteEntretienById(@PathVariable Long id) {
        return entretienService.deleteEntretienById(id);
    }
}