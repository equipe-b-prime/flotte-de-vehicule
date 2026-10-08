package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Représente le contrat de location d'un véhicule.
 */
@Entity
@Table(name = "contrat")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_contrat")
    private String typeContrat;

    @Column(name = "duree_mois")
    private int dureeMois;

    @Column(name = "loyer_mensuel")
    private BigDecimal loyerMensuel;

    @Column(name = "km_autorise")
    private int kmAutorise;
}