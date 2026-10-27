package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.service.EntretienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entretiens")
@RequiredArgsConstructor
public class EntretienController {
    private final EntretienService entretienService;

    @PostMapping
    public EntretienResponseDto createEntretien(@RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.createEntretien(entretienRequest);
    }

    @GetMapping("/{id}")
    public EntretienResponseDto getEntretienById(@PathVariable Long id) {
        return entretienService.getEntretienById(id);
    }

    @GetMapping
    public Page<EntretienResponseDto> getAllEntretiens(Pageable pageable) {
        return entretienService.getAllEntretiensPagine(pageable);
    }

    @PutMapping("/{id}")
    public EntretienResponseDto updateEntretienById(@PathVariable Long id, @RequestBody EntretienRequestDto entretienRequest) {
        return entretienService.updateEntretienById(id, entretienRequest);
    }

    @DeleteMapping("/{id}")
    public EntretienResponseDto deleteEntretienById(@PathVariable Long id) {
        return entretienService.deleteEntretienById(id);
    }
}