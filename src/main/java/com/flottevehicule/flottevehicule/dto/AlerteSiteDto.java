package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlerteSiteDto {
    private Long siteId;
    private String nomSite;
    private List<AlerteVehiculeDto> alertes;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlerteVehiculeDto {
        private Long vehiculeId;
        private String immatriculation;
        private String marque;
        private String modele;
        private LocalDateTime echeanceDate;
        private Integer echeanceKm;
        private Long ecartJours;
        private Integer ecartKm;
        private String statut; // "DEPASSE", "IMMINENT_DATE", "IMMINENT_KM"
    }
}

