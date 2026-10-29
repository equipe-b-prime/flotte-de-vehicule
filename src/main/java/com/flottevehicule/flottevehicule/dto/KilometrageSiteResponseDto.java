package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KilometrageSiteResponseDto {
    private Long siteId;
    private String nomSite;
    private String mois;
    private Long totalKilometrage;
    private List<VehiculeKilometrageDto> vehicules;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehiculeKilometrageDto {
        private Long vehiculeId;
        private String immatriculation;
        private String marque;
        private String modele;
        private Long kmParcouru;
    }
}

