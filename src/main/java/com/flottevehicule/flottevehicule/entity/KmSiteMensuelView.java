package com.flottevehicule.flottevehicule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "v_km_site_mensuel")
@Immutable
@Getter
@NoArgsConstructor
public class KmSiteMensuelView {

    @Id
    private Long id;
    private Long vehiculeId;
    private Long siteId;
    private LocalDateTime mois;
    private Long kmParcouru;
}

