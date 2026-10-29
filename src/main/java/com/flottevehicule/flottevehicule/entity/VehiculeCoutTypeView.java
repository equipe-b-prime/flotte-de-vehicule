package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Table(name = "v_vehicule_cout_type")
@Immutable
@Getter
@NoArgsConstructor
public class VehiculeCoutTypeView {

    @Id
    private Long id;
    private Long vehiculeId;
    private Long typeEntretienId;
    private String libelle;
    private Long nbEntretiens;
    private BigDecimal coutTotal;
}

