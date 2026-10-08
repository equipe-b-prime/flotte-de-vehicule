package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.PrestataireResponseDto;
import com.flottevehicule.flottevehicule.entity.Prestataire;
import com.flottevehicule.flottevehicule.repository.PrestataireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrestataireService  {

    private final PrestataireRepository prestataireRepository;
    public PrestataireResponseDto toDto(Prestataire prestataire) {
        if (prestataire == null) {
            return null;
        }
        return PrestataireResponseDto.builder()
                .id(prestataire.getId())
                .nom(prestataire.getNom())
                .build();
    }
}
