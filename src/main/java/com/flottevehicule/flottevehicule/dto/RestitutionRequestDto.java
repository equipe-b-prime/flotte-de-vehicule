package com.flottevehicule.flottevehicule.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestitutionRequestDto {
    private LocalDateTime dateRestitution;
    private Integer kilometrage;
}