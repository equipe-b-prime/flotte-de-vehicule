package com.flottevehicule.flottevehicule.entity;

import com.flottevehicule.flottevehicule.entity.Affectation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Représente une dépense liée à une affectation.
 */
@Entity
@Table(name = "depense")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Depense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_depense")
    private LocalDateTime dateDepense;

    @Column(name = "montant_depense")
    private BigDecimal montantDepense;

    @ManyToOne
    @JoinColumn(name = "type_depense_id")
    private TypeDepense typeDepense;

    @ManyToOne
    @JoinColumn(name = "affectation_id")
    private Affectation affectation;
}