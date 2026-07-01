package itu.GreenField.repository;

import itu.GreenField.model.Transferts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransfertsRepository extends JpaRepository<Transferts, Long> {

  @Query("""
      SELECT t FROM Transferts t
      WHERE (COALESCE(:dateDebut, NULL) IS NULL OR t.dateTransfert >= :dateDebut)
        AND (COALESCE(:dateFin, NULL) IS NULL OR t.dateTransfert <= :dateFin)
        AND (COALESCE(:codePointDeVente, NULL) IS NULL
             OR t.pointDeVenteSource.code = :codePointDeVente
             OR t.pointDeVenteCible.code = :codePointDeVente)
      ORDER BY t.dateTransfert DESC
      """)
  List<Transferts> rechercherAvecFiltres(
      @Param("dateDebut") LocalDateTime dateDebut,
      @Param("dateFin") LocalDateTime dateFin,
      @Param("codePointDeVente") String codePointDeVente);
}
