package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoutVehiculeResponseDto {
    private Long vehiculeId;
    private String immatriculation;
    private BigDecimal coutTotal;
    private Double coutParKilometre;
    private Integer kilometrageParcouru;
    private List<RepartitionCoutDto> repartitionParType;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RepartitionCoutDto {
        private Long typeEntretienId;
        private String libelle;
        private Long nbEntretiens;
        private BigDecimal coutTotal;
    }
}

