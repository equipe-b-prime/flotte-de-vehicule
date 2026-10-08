package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.SalarieService;

/**
 * Contrôleur REST exposant les endpoints de gestion des salariés.
 */
@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalarieController {
    private final SalarieService salarieService;

    /**
     * Crée un nouveau salarié.
     *
     * @param salarieRequest les informations du salarié à créer
     * @return le salarié créé
     */
    @PostMapping
    public SalarieResponseDto createSalarie(@RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.createSalarie(salarieRequest);
    }

    /**
     * Récupère un salarié à partir de son identifiant.
     *
     * @param id L'identifiant du salarié à récupérer.
     * @return SalarieResponseDto Les détails du salarié récupéré.
     */
    @GetMapping("/{id}")
    public SalarieResponseDto getSalarieById(@PathVariable Long id) {
        return salarieService.getSalarieById(id);
    }

    /**
     * Récupère la liste de tous les salariés.
     *
     * @return List<SalarieResponseDto> La liste des salariés récupérés.
     */
    @GetMapping
    public List<SalarieResponseDto> getAllSalaries() {
        return salarieService.getAllSalaries();
    }

    /**
     * Modifie un salarié existant.
     *
     * @param id L'identifiant du salarié à modifier.
     * @param salarieRequest Les données de la requête pour modifier le salarié.
     * @return SalarieResponseDto Les détails du salarié modifié.
     */
    @PutMapping("/{id}")
    public SalarieResponseDto updateSalarieById(@PathVariable Long id, @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.updateSalarieById(id, salarieRequest);
    }

    /**
     * Supprime un salarié à partir de son identifiant.
     *
     * @param id L'identifiant du salarié à supprimer.
     * @return SalarieResponseDto Les détails du salarié supprimé.
     */
    @DeleteMapping("/{id}")
    public SalarieResponseDto deleteSalarieById(@PathVariable Long id) {
        return salarieService.deleteSalarieById(id);
    }
}