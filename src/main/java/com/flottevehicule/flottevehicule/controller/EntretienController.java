package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.EntretienService;

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
    public Page<EntretienResponseDto> getAllEntretiens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateIntervention,desc") String sort) {
        if (size > 100) size = 100;
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        return entretienService.getAllEntretiensPaginated(pageable);
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
