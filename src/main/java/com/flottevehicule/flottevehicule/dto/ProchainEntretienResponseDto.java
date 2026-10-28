package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProchainEntretienResponseDto {
    private LocalDateTime dateEstimee;
    private Integer kilometrageCible;
    private String seuilDeclencheur;
}