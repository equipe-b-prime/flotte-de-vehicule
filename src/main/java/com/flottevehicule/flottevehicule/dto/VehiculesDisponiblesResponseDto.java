package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculesDisponiblesResponseDto {
    private String periodeDebut;
    private String periodeFin;
    private Map<String, List<VehiculeDisponibleDto>> vehiculesParSite;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehiculeDisponibleDto {
        private Long vehiculeId;
        private String immatriculation;
        private String marque;
        private String modele;
        private Integer kilometrageActuel;
        private LocalDateTime prochaineEcheanceEntretien;
        private Integer kilometrageCibleEntretien;
    }
}

