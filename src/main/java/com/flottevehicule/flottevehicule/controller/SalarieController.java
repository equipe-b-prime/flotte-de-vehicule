package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.SalarieRequestDto;
import com.flottevehicule.flottevehicule.dto.SalarieResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.flottevehicule.flottevehicule.service.AffectationService;
import com.flottevehicule.flottevehicule.service.SalarieService;

@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalarieController {
    private final SalarieService salarieService;
    private final AffectationService affectationService;

    @PostMapping
    public SalarieResponseDto createSalarie(@RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.createSalarie(salarieRequest);
    }

    @GetMapping("/{id}")
    public SalarieResponseDto getSalarieById(@PathVariable Long id) {
        return salarieService.getSalarieById(id);
    }

    @GetMapping
    public List<SalarieResponseDto> getAllSalaries() {
        return salarieService.getAllSalaries();
    }

    @PutMapping("/{id}")
    public SalarieResponseDto updateSalarieById(@PathVariable Long id, @RequestBody SalarieRequestDto salarieRequest) {
        return salarieService.updateSalarieById(id, salarieRequest);
    }

    @DeleteMapping("/{id}")
    public SalarieResponseDto deleteSalarieById(@PathVariable Long id) {
        return salarieService.deleteSalarieById(id);
    }

    @GetMapping("/{id}/affectations")
    public List<AffectationResponseDto> getAffectationsBySalarie(@PathVariable Long id) {
        return affectationService.getAffectationsBySalarie(id);
    }
}
