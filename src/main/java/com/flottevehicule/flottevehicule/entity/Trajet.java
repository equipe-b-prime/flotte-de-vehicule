package com.flottevehicule.flottevehicule.entity;

import com.flottevehicule.flottevehicule.entity.Affectation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Représente un trajet effectué dans le cadre d'une affectation.
 */
@Entity
@Table(name = "trajet")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trajet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "km_depart")
    private int kmDepart;

    @Column(name = "km_arrivee")
    private int kmArrivee;

    @Column(name = "date_trajet")
    private Date dateTrajet;

    private String motif;

    @ManyToOne
    @JoinColumn(name = "affectation_id")
    private Affectation affectation;
}