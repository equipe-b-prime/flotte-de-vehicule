package com.flottevehicule.flottevehicule.controller;

import com.flottevehicule.flottevehicule.dto.AffectationRequestDto;
import com.flottevehicule.flottevehicule.dto.AffectationResponseDto;
import com.flottevehicule.flottevehicule.dto.RestitutionRequestDto;
import com.flottevehicule.flottevehicule.service.AffectationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/affectations")
@RequiredArgsConstructor
public class AffectationController {
    private final AffectationService affectationService;

    @PostMapping
    public AffectationResponseDto createAffectation(@RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.createAffectation(affectationRequest);
    }

    @GetMapping("/{id}")
    public AffectationResponseDto getAffectationById(@PathVariable Long id) {
        return affectationService.getAffectationById(id);
    }

    @GetMapping
    public List<AffectationResponseDto> getAllAffectations() {
        return affectationService.getAllAffectations();
    }

    @PutMapping("/{id}")
    public AffectationResponseDto updateAffectationById(@PathVariable Long id, @RequestBody AffectationRequestDto affectationRequest) {
        return affectationService.updateAffectationById(id, affectationRequest);
    }

    @DeleteMapping("/{id}")
    public AffectationResponseDto deleteAffectationById(@PathVariable Long id) {
        return affectationService.deleteAffectationById(id);
    }

    @PostMapping("/{id}/restitution")
    public AffectationResponseDto restituerAffectation(@PathVariable Long id, @RequestBody RestitutionRequestDto restitutionRequest) {
        return affectationService.restituerAffectation(id, restitutionRequest);
    }
}