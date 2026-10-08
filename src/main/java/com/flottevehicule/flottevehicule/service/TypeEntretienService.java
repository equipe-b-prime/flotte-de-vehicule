package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.TypeEntretienResponseDto;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;
import com.flottevehicule.flottevehicule.repository.TypeEntretienRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class TypeEntretienService {
    private final TypeEntretienRepository typeEntretienRepository;
    public TypeEntretienResponseDto toDto(TypeEntretien typeEntretien) {
        if (typeEntretien == null) {
            return null;
        }
        return TypeEntretienResponseDto.builder()
                .id(typeEntretien.getId())
                .libelle(typeEntretien.getLibelle())
                .estRegulier(typeEntretien.getEstRegulier())
                .build();
    }

}
