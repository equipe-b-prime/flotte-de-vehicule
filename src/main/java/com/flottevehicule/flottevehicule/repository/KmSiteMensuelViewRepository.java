package com.flottevehicule.flottevehicule.repository;

import com.flottevehicule.flottevehicule.entity.KmSiteMensuelView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KmSiteMensuelViewRepository extends JpaRepository<KmSiteMensuelView, Long> {
    List<KmSiteMensuelView> findBySiteIdAndMois(Long siteId, LocalDateTime mois);

    @Query("SELECT k FROM KmSiteMensuelView k WHERE k.siteId = :siteId " +
           "AND k.mois >= :debutMois AND k.mois < :finMois")
    List<KmSiteMensuelView> findBySiteIdAndPeriode(
            @Param("siteId") Long siteId,
            @Param("debutMois") LocalDateTime debutMois,
            @Param("finMois") LocalDateTime finMois);
}

