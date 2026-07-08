package itu.greenField.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.Tresorerie;
import itu.greenField.model.TypeFlux;

public interface TresorerieRepository extends JpaRepository<Tresorerie, Integer> {

    Page<Tresorerie> findAllByOrderByDateOperationDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Tresorerie t " +
            "WHERE t.typeMouvement = :type " +
            "AND t.dateOperation BETWEEN :debut AND :fin")
    BigDecimal sommeParType(@Param("type") TypeFlux type,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
}
