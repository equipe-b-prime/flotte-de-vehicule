package com.flottevehicule.flottevehicule.entity;

import com.flottevehicule.flottevehicule.entity.Contrat;
import com.flottevehicule.flottevehicule.entity.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Représente un véhicule de la flotte, rattaché à un site et à un contrat.
 */
@Entity
@Table(name = "vehicule")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String immatriculation;
    private String marque;
    private String modele;
    @Column(name = "date_mise_en_circulation")
    private LocalDateTime dateMiseEnCirculation;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
    @ManyToOne
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;
}