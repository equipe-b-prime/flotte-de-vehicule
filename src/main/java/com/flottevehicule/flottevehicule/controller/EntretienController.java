package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entretiens")
@RequiredArgsConstructor
@Tag(name = "Entretiens", description = "Gestion des opérations de maintenance et d'entretien de la flotte")
public class EntretienController {

    private final EntretienService entretienService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enregistrer un nouvel entretien")
    public EntretienResponseDto createEntretien(@Valid @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.createEntretien(entretienRequest);
    }

    @GetMapping
    @Operation(summary = "Lister tous les entretiens (paginé)")
    public Page<EntretienResponseDto> getAllEntretiens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateIntervention,desc") String sort) {
        return entretienService.getAllEntretiensPaginated(page, size, sort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un entretien par son ID")
    public EntretienResponseDto getEntretienById(@PathVariable Long id) {
        return entretienService.getEntretienById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un entretien existant")
    public EntretienResponseDto updateEntretienById(@PathVariable Long id, @Valid @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.updateEntretienById(id, entretienRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un enregistrement d'entretien")
    public EntretienResponseDto deleteEntretienById(@PathVariable Long id) {
        return entretienService.deleteEntretienById(id);
    }
}