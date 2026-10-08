package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.ContratResponseDto;
import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.repository.ContratRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContratService {
    private final ContratRepository contratRepository;
    /**
     * Construit la réponse d'un contrat.
     *
     * @param contrat L'entité contrat (peut être null).
     * @return ContratResponseDto La réponse du contrat ou null.
     */

    public ContratResponseDto toDto(Contrat contrat) {
        if (contrat == null) {
            return null;
        }
        return ContratResponseDto.builder()
                .id(contrat.getId())
                .typeContrat(contrat.getTypeContrat())
                .dureeMois(contrat.getDureeMois())
                .loyerMensuel(contrat.getLoyerMensuel())
                .kmAutorise(contrat.getKmAutorise())
                .build();
    }
}
