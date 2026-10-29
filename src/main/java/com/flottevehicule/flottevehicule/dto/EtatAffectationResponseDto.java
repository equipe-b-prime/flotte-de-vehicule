package com.flottevehicule.flottevehicule.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtatAffectationResponseDto {
    private Long id;
    private String code;
    private String libelle;
}

