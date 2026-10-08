package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Réponse contenant les données d'un contrat.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ContratResponseDto {
    private Long id;
    private String typeContrat;
    private int dureeMois;
    private BigDecimal loyerMensuel;
    private int kmAutorise;
}