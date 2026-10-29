package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.CoutVehiculeResponseDto;
import com.flottevehicule.flottevehicule.dto.KilometrageSiteResponseDto;
import com.flottevehicule.flottevehicule.dto.VehiculesDisponiblesResponseDto;
import com.flottevehicule.flottevehicule.dto.ProchainEntretienResponseDto;
import com.flottevehicule.flottevehicule.entity.*;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestitutionReportingService {

    private final VehiculeRepository vehiculeRepository;
    private final SiteRepository siteRepository;
    private final AffectationRepository affectationRepository;
    private final EntretienRepository entretienRepository;
    private final VehiculeService vehiculeService;
    private final VehiculeStatsViewRepository vehiculeStatsViewRepository;
    private final VehiculeCoutTypeViewRepository vehiculeCoutTypeViewRepository;
    private final KmSiteMensuelViewRepository kmSiteMensuelViewRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * F17. Lister les véhicules disponibles sur une période.
     */
    public VehiculesDisponiblesResponseDto getVehiculesDisponibles(String duStr, String auStr) {
        LocalDateTime debut;
        LocalDateTime fin;

        if (duStr == null || duStr.isBlank() || auStr == null || auStr.isBlank()) {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
            debut = startOfWeek.atStartOfDay();
            fin = endOfWeek.atTime(23, 59, 59);
        } else {
            try {
                debut = LocalDateTime.parse(duStr, DATE_FORMATTER);
                fin = LocalDateTime.parse(auStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                try {
                    debut = LocalDate.parse(duStr).atStartOfDay();
                    fin = LocalDate.parse(auStr).atTime(23, 59, 59);
                } catch (Exception ex) {
                    throw new RequeteInvalideException("Format de date invalide (attendu dd-MM-yyyy HH:mm:ss ou yyyy-MM-dd)");
                }
            }
        }

        if (fin.isBefore(debut)) {
            throw new RequeteInvalideException("La date de fin ne peut pas précéder la date de début");
        }

        List<Vehicule> tousVehicules = vehiculeRepository.findAll();
        List<Vehicule> disponibles = new ArrayList<>();

        final LocalDateTime debutFinal = debut;
        final LocalDateTime finFinal = fin;

        for (Vehicule v : tousVehicules) {
            // Vérifier affectation
            boolean aAffectation = !affectationRepository.findChevauchements(v.getId(), debutFinal, finFinal).isEmpty();

            // Vérifier immobilisation entretien
            boolean aImmobilisation = entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(v.getId()).stream()
                    .anyMatch(e -> {
                        LocalDateTime debutImmob = e.getDateIntervention();
                        LocalDateTime finImmob = e.getDateIntervention().plusDays(e.getDureeImmobilisatin());
                        return debutFinal.isBefore(finImmob) && finFinal.isAfter(debutImmob);
                    });

            if (!aAffectation && !aImmobilisation) {
                disponibles.add(v);
            }
        }

        // Grouper par site
        Map<String, List<VehiculesDisponiblesResponseDto.VehiculeDisponibleDto>> map = new LinkedHashMap<>();
        for (Vehicule v : disponibles) {
            String siteNom = (v.getSite() != null) ? v.getSite().getNom() : "Non assigné";
            ProchainEntretienResponseDto prochain = vehiculeService.calculerProchainEntretien(v.getId());

            VehiculesDisponiblesResponseDto.VehiculeDisponibleDto dto = VehiculesDisponiblesResponseDto.VehiculeDisponibleDto.builder()
                    .vehiculeId(v.getId())
                    .immatriculation(v.getImmatriculation())
                    .marque(v.getMarque())
                    .modele(v.getModele())
                    .kilometrageActuel(v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0)
                    .prochaineEcheanceEntretien(prochain.getDateEstimee())
                    .kilometrageCibleEntretien(prochain.getKilometrageCible())
                    .build();

            map.computeIfAbsent(siteNom, k -> new ArrayList<>()).add(dto);
        }

        return VehiculesDisponiblesResponseDto.builder()
                .periodeDebut(debut.format(DATE_FORMATTER))
                .periodeFin(fin.format(DATE_FORMATTER))
                .vehiculesParSite(map)
                .build();
    }

    /**
     * F18. Calculer le coût total d'un véhicule (utilise la vue SQL v_vehicule_stats et v_vehicule_cout_type).
     */
    public CoutVehiculeResponseDto getCoutTotalVehicule(Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + vehiculeId));

        BigDecimal coutTotal = BigDecimal.ZERO;
        Optional<VehiculeStatsView> statsOpt = vehiculeStatsViewRepository.findByVehiculeId(vehiculeId);
        if (statsOpt.isPresent() && statsOpt.get().getCoutTotalEntretiens() != null) {
            coutTotal = statsOpt.get().getCoutTotalEntretiens();
        }

        // Kilométrage parcouru (différence ou kilometrage actuel)
        int kmParcouru = vehicule.getKilometrageActuel() != null ? vehicule.getKilometrageActuel() : 0;

        Double coutParKm = 0.0;
        if (kmParcouru > 0 && coutTotal.compareTo(BigDecimal.ZERO) > 0) {
            coutParKm = coutTotal.divide(BigDecimal.valueOf(kmParcouru), 4, RoundingMode.HALF_UP).doubleValue();
        }

        // Répartition par type depuis la vue v_vehicule_cout_type
        List<VehiculeCoutTypeView> repartitionViews = vehiculeCoutTypeViewRepository.findByVehiculeId(vehiculeId);
        List<CoutVehiculeResponseDto.RepartitionCoutDto> repartition = repartitionViews.stream()
                .map(v -> CoutVehiculeResponseDto.RepartitionCoutDto.builder()
                        .typeEntretienId(v.getTypeEntretienId())
                        .libelle(v.getLibelle())
                        .nbEntretiens(v.getNbEntretiens())
                        .coutTotal(v.getCoutTotal())
                        .build())
                .collect(Collectors.toList());

        return CoutVehiculeResponseDto.builder()
                .vehiculeId(vehicule.getId())
                .immatriculation(vehicule.getImmatriculation())
                .coutTotal(coutTotal)
                .coutParKilometre(coutParKm)
                .kilometrageParcouru(kmParcouru)
                .repartitionParType(repartition)
                .build();
    }

    /**
     * F19. Mesurer le kilométrage par site (utilise la vue SQL v_km_site_mensuel et relevés).
     */
    public KilometrageSiteResponseDto getKilometrageParSite(Long siteId, String moisStr) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site non trouvé avec l'id " + siteId));

        YearMonth ym;
        try {
            ym = (moisStr != null && !moisStr.isBlank()) ? YearMonth.parse(moisStr) : YearMonth.now();
        } catch (DateTimeParseException e) {
            throw new RequeteInvalideException("Format de mois invalide (attendu YYYY-MM, ex: 2026-11)");
        }

        LocalDateTime debutMois = ym.atDay(1).atStartOfDay();
        LocalDateTime finMois = ym.plusMonths(1).atDay(1).atStartOfDay();

        // Consulter la vue v_km_site_mensuel
        List<KmSiteMensuelView> kmViews = kmSiteMensuelViewRepository.findBySiteIdAndPeriode(siteId, debutMois, finMois);

        Map<Long, Long> kmParVehiculeMap = new LinkedHashMap<>();
        for (KmSiteMensuelView v : kmViews) {
            kmParVehiculeMap.put(v.getVehiculeId(), v.getKmParcouru());
        }

        // Compléter avec les véhicules rattachés au site
        List<Vehicule> vehiculesDuSite = vehiculeRepository.findBySiteIdOrderByImmatriculationAsc(siteId);
        List<KilometrageSiteResponseDto.VehiculeKilometrageDto> vehiculeList = new ArrayList<>();

        long totalSite = 0L;
        for (Vehicule veh : vehiculesDuSite) {
            long km = kmParVehiculeMap.getOrDefault(veh.getId(), 0L);
            totalSite += km;

            vehiculeList.add(KilometrageSiteResponseDto.VehiculeKilometrageDto.builder()
                    .vehiculeId(veh.getId())
                    .immatriculation(veh.getImmatriculation())
                    .marque(veh.getMarque())
                    .modele(veh.getModele())
                    .kmParcouru(km)
                    .build());
        }

        return KilometrageSiteResponseDto.builder()
                .siteId(site.getId())
                .nomSite(site.getNom())
                .mois(ym.toString())
                .totalKilometrage(totalSite)
                .vehicules(vehiculeList)
                .build();
    }
}
