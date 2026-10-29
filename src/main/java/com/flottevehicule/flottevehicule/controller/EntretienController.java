package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entretiens")
@RequiredArgsConstructor
public class EntretienController {

    private final EntretienService entretienService;

    @PostMapping
    public EntretienResponseDto createEntretien(@RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.createEntretien(entretienRequest);
    }

    @GetMapping
    public Page<EntretienResponseDto> getAllEntretiens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateIntervention,desc") String sort) {
        return entretienService.getAllEntretiensPaginated(page, size, sort);
    }

    @PutMapping("/{id}")
    public EntretienResponseDto updateEntretienById(@PathVariable Long id, @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.updateEntretienById(id, entretienRequest);
    }

    @GetMapping("/{id}")
    public EntretienResponseDto getEntretienById(@PathVariable Long id) {
        return entretienService.getEntretienById(id);
    }

    @DeleteMapping("/{id}")
    public EntretienResponseDto deleteEntretienById(@PathVariable Long id) {
        return entretienService.deleteEntretienById(id);
    }
}
