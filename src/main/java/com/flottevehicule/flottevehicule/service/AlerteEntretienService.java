package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class AlerteEntretienService {

    private final VehiculeRepository vehiculeRepository;
    private final EntretienRepository entretienRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void signalerEntretiensDus() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();

        for (Vehicule v : vehicules) {
            Optional<Entretien> dernierEntretien = entretienRepository
                    .findByVehiculeIdOrderByDateInterventionDesc(v.getId())
                    .stream().findFirst();

            LocalDateTime pointDepart;
            int kilometrageDepart;

            if (dernierEntretien.isPresent() && dernierEntretien.get().getTypeEntretien().getEstRegulier()) {
                pointDepart = dernierEntretien.get().getDateIntervention();
                kilometrageDepart = dernierEntretien.get().getKilometrage() != null ?
                        dernierEntretien.get().getKilometrage() : 0;
            } else {
                pointDepart = v.getDateMiseEnCirculation();
                kilometrageDepart = 0;
            }

            LocalDateTime echeance = pointDepart.plusYears(1);
            int kmCible = kilometrageDepart + 15000;
            int kmActuel = v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0;

            boolean estProcheDate = LocalDateTime.now().plusDays(30).isAfter(echeance);
            boolean estProcheKm = kmActuel >= kmCible - 1000;
            boolean estDepasse = LocalDateTime.now().isAfter(echeance) || kmActuel >= kmCible;

            if (estDepasse || estProcheDate || estProcheKm) {
                String raison;
                if (estDepasse) {
                    raison = "Échéance d'entretien dépassée pour le véhicule " + v.getImmatriculation();
                } else if (estProcheDate) {
                    raison = "Entretien imminent (date) pour le véhicule " + v.getImmatriculation();
                } else {
                    raison = "Entretien imminent (kilométrage) pour le véhicule " + v.getImmatriculation();
                }
                System.out.println("[ALERTE] " + raison);
            }
        }
    }
}