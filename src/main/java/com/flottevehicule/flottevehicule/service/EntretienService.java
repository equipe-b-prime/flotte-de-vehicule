package com.flottevehicule.flottevehicule.service;

import com.flottevehicule.flottevehicule.dto.AlerteSiteDto;
import com.flottevehicule.flottevehicule.dto.EntretienRequestDto;
import com.flottevehicule.flottevehicule.dto.EntretienResponseDto;
import com.flottevehicule.flottevehicule.dto.RequestDto;
import com.flottevehicule.flottevehicule.dto.ResponseDto;
import com.flottevehicule.flottevehicule.entity.Entretien;
import com.flottevehicule.flottevehicule.entity.Prestataire;
import com.flottevehicule.flottevehicule.entity.Site;
import com.flottevehicule.flottevehicule.entity.TypeEntretien;
import com.flottevehicule.flottevehicule.entity.Vehicule;
import com.flottevehicule.flottevehicule.exception.NotFoundException;
import com.flottevehicule.flottevehicule.exception.RequeteInvalideException;
import com.flottevehicule.flottevehicule.repository.EntretienRepository;
import com.flottevehicule.flottevehicule.repository.PrestataireRepository;
import com.flottevehicule.flottevehicule.repository.TypeEntretienRepository;
import com.flottevehicule.flottevehicule.repository.VehiculeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EntretienService extends AbstractCrudService {

    private final EntretienRepository entretienRepository;
    private final VehiculeRepository vehiculeRepository;
    private final TypeEntretienRepository typeEntretienRepository;
    private final PrestataireRepository prestataireRepository;
    private final PrestataireService prestataireService;
    private final TypeEntretienService typeEntretienService;
    private final VehiculeService vehiculeService;

    // F16 : Traçabilité mémoire pour éviter de resignaler le même véhicule avant son prochain entretien
    private final Set<Long> vehiculesSignales = ConcurrentHashMap.newKeySet();

    public EntretienService(EntretienRepository entretienRepository,
                            VehiculeRepository vehiculeRepository,
                            TypeEntretienRepository typeEntretienRepository,
                            PrestataireRepository prestataireRepository,
                            PrestataireService prestataireService,
                            TypeEntretienService typeEntretienService,
                            VehiculeService vehiculeService) {
        super(entretienRepository);
        this.entretienRepository = entretienRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.typeEntretienRepository = typeEntretienRepository;
        this.prestataireRepository = prestataireRepository;
        this.prestataireService = prestataireService;
        this.typeEntretienService = typeEntretienService;
        this.vehiculeService = vehiculeService;
    }

    @Override
    public ResponseDto toDto(Object entity) {
        return toDto((Entretien) entity);
    }

    @Override
    public ResponseDto create(RequestDto request) {
        return createEntretien((EntretienRequestDto) request);
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        return updateEntretienById(id, (EntretienRequestDto) request);
    }

    public EntretienResponseDto toDto(Entretien entretien) {
        return EntretienResponseDto.builder()
                .id(entretien.getId())
                .dateIntervention(entretien.getDateIntervention())
                .cout(entretien.getCout())
                .dureeImmobilisatin(entretien.getDureeImmobilisatin())
                .kilometrage(entretien.getKilometrage())
                .vehicule(vehiculeService.toDto(entretien.getVehicule()))
                .typeEntretien(typeEntretienService.toDto(entretien.getTypeEntretien()))
                .prestataire(prestataireService.toDto(entretien.getPrestataire()))
                .build();
    }

    public Entretien toEntity(EntretienRequestDto entretienRequest, Vehicule vehicule,
                              TypeEntretien typeEntretien, Prestataire prestataire) {
        return Entretien.builder()
                .dateIntervention(entretienRequest.getDateIntervention())
                .cout(entretienRequest.getCout())
                .dureeImmobilisatin(entretienRequest.getDureeImmobilisatin())
                .kilometrage(entretienRequest.getKilometrage())
                .vehicule(vehicule)
                .typeEntretien(typeEntretien)
                .prestataire(prestataire)
                .build();
    }

    public EntretienResponseDto createEntretien(EntretienRequestDto entretienRequest) {
        Vehicule vehicule = vehiculeRepository.findById(entretienRequest.getVehiculeId())
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé"));
        TypeEntretien typeEntretien = typeEntretienRepository.findById(entretienRequest.getTypeEntretienId())
                .orElseThrow(() -> new NotFoundException("Type d'entretien non trouvé"));
        Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                .orElseThrow(() -> new NotFoundException("Prestataire non trouvé"));

        if (entretienRequest.getCout() == null || entretienRequest.getCout().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequeteInvalideException("Le coût doit être positif");
        }

        if (entretienRequest.getDateIntervention() != null && vehicule.getDateMiseEnCirculation() != null &&
                entretienRequest.getDateIntervention().isBefore(vehicule.getDateMiseEnCirculation())) {
            throw new RequeteInvalideException("La date d'entretien ne peut pas précéder la mise en service du véhicule");
        }

        Integer dernierKm = entretienRepository.findDernierKilometrage(vehicule.getId());
        if (dernierKm != null && entretienRequest.getKilometrage() != null &&
                entretienRequest.getKilometrage() <= dernierKm) {
            throw new RequeteInvalideException(
                    "Le kilométrage doit être supérieur au dernier kilométrage connu (" + dernierKm + " km)");
        }

        return toDto(entretienRepository.save(toEntity(entretienRequest, vehicule, typeEntretien, prestataire)));
    }

    public EntretienResponseDto getEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        return toDto(entretien);
    }

    public List<EntretienResponseDto> getAllEntretiens() {
        return entretienRepository.findAllByOrderByDateInterventionDesc().stream()
                .map(this::toDto)
                .toList();
    }

    public Page<EntretienResponseDto> getAllEntretiensPaginated(int page, int size, String sort) {
        if (size > 100) size = 100;
        if (size <= 0) size = 20;
        if (page < 0) page = 0;

        String[] sortParts = (sort != null && !sort.isBlank()) ? sort.split(",") : new String[]{"dateIntervention", "desc"};
        String sortProperty = sortParts[0];
        if ("date".equalsIgnoreCase(sortProperty)) {
            sortProperty = "dateIntervention";
        }
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        return entretienRepository.findAll(pageable).map(this::toDto);
    }

    public List<EntretienResponseDto> getEntretiensByVehicule(Long vehiculeId) {
        vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Véhicule non trouvé avec l'id " + vehiculeId));
        return entretienRepository.findByVehiculeIdOrderByDateInterventionDesc(vehiculeId).stream()
                .map(this::toDto)
                .toList();
    }

    public EntretienResponseDto updateEntretienById(Long id, EntretienRequestDto entretienRequest) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        entretien.setCout(entretienRequest.getCout());
        if (entretienRequest.getPrestataireId() != null) {
            Prestataire prestataire = prestataireRepository.findById(entretienRequest.getPrestataireId())
                    .orElseThrow(() -> new NotFoundException("Prestataire non trouvé"));
            entretien.setPrestataire(prestataire);
        }
        return toDto(entretienRepository.save(entretien));
    }

    public EntretienResponseDto deleteEntretienById(Long id) {
        Entretien entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entretien non trouvé avec l'id " + id));
        entretienRepository.deleteById(id);
        return toDto(entretien);
    }

    /**
     * F16. Signaler les entretiens dus.
     * Déclencheur : tâche planifiée, exécutée chaque nuit.
     * Règle : un véhicule est signalé s'il est à moins de 1 000 km ou moins de trente jours de son échéance,
     * ou s'il l'a déjà dépassée.
     * Effet : une alerte est produite par site, avec le véhicule, l'échéance et l'écart.
     * Traçabilité : un véhicule déjà signalé n'est pas signalé une seconde fois avant son entretien.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void tachePlanifieeNocturneEntretiensDus() {
        log.info("[SCHEDULED F16] Démarrage du contrôle des entretiens dus (toutes les 10s)...");
        Map<String, AlerteSiteDto> alertes = executerDetectionAlertes();
        log.info("[SCHEDULED F16] Contrôle terminé. Sites avec alertes : {}", alertes.size());
    }

    public Map<String, AlerteSiteDto> executerDetectionAlertes() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        Map<String, AlerteSiteDto> resultatsParSite = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (Vehicule v : vehicules) {
            Optional<Entretien> dernierEntretien = entretienRepository
                    .findByVehiculeIdOrderByDateInterventionDesc(v.getId())
                    .stream().findFirst();

            LocalDateTime pointDepart;
            int kilometrageDepart;

            if (dernierEntretien.isPresent() && dernierEntretien.get().getTypeEntretien() != null
                    && Boolean.TRUE.equals(dernierEntretien.get().getTypeEntretien().getEstRegulier())) {
                pointDepart = dernierEntretien.get().getDateIntervention();
                kilometrageDepart = dernierEntretien.get().getKilometrage() != null ?
                        dernierEntretien.get().getKilometrage().intValue() : 0;
            } else {
                pointDepart = v.getDateMiseEnCirculation() != null ? v.getDateMiseEnCirculation() : now;
                kilometrageDepart = 0;
            }

            LocalDateTime echeanceDate = pointDepart.plusYears(1);
            int kmCible = kilometrageDepart + 15000;
            int kmActuel = v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0;

            long ecartJours = ChronoUnit.DAYS.between(now, echeanceDate);
            int ecartKm = kmCible - kmActuel;

            boolean depasseDate = now.isAfter(echeanceDate);
            boolean depasseKm = kmActuel >= kmCible;
            boolean procheDate = ecartJours <= 30 && ecartJours >= 0;
            boolean procheKm = ecartKm <= 1000 && ecartKm >= 0;

            if (depasseDate || depasseKm || procheDate || procheKm) {
                // Traçabilité : ne pas resignaler si déjà signalé avant son entretien
                if (vehiculesSignales.contains(v.getId())) {
                    continue;
                }

                String statut;
                if (depasseDate || depasseKm) {
                    statut = "DEPASSE";
                } else if (procheDate) {
                    statut = "IMMINENT_DATE";
                } else {
                    statut = "IMMINENT_KM";
                }

                vehiculesSignales.add(v.getId());

                Site site = v.getSite();
                String siteCle = (site != null) ? site.getNom() : "Non assigné";
                Long siteId = (site != null) ? site.getId() : null;

                AlerteSiteDto siteDto = resultatsParSite.computeIfAbsent(siteCle, k -> AlerteSiteDto.builder()
                        .siteId(siteId)
                        .nomSite(siteCle)
                        .alertes(new ArrayList<>())
                        .build());

                siteDto.getAlertes().add(AlerteSiteDto.AlerteVehiculeDto.builder()
                        .vehiculeId(v.getId())
                        .immatriculation(v.getImmatriculation())
                        .marque(v.getMarque())
                        .modele(v.getModele())
                        .echeanceDate(echeanceDate)
                        .echeanceKm(kmCible)
                        .ecartJours(ecartJours)
                        .ecartKm(ecartKm)
                        .statut(statut)
                        .build());

                log.warn("[ALERTE ENTRETIEN] Site: {} | Véhicule: {} ({}) | Statut: {} | Écart jours: {} | Écart km: {}",
                        siteCle, v.getImmatriculation(), v.getMarque(), statut, ecartJours, ecartKm);
            } else {
                // Si le véhicule est de nouveau en règle (ex: entretien réalisé), réinitialiser
                vehiculesSignales.remove(v.getId());
            }
        }

        return resultatsParSite;
    }
}
