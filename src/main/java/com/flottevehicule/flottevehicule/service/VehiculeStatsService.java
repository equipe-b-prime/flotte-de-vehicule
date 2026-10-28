package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.VehiculeStatsResponseDto;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculeStatsService {

    private final VehiculeRepository vehiculeRepository;

    public VehiculeStatsResponseDto getStatsBySite(Long siteId) {
        // Stats simples par site
        VehiculeStatsResponseDto dto = new VehiculeStatsResponseDto();
        // À implémenter selon besoins réels
        return dto;
    }
}