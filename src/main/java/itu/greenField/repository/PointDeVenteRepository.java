package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.PointDeVente;

import java.time.LocalDateTime;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Integer> {
    List<PointDeVente> findByCodeContainingIgnoreCase(String code);

    @Query(value = "SELECT msf.idproduit, " +
           "SUM(CASE " +
           "  WHEN ms.type_mouvement IN ('Entree_Boutique', 'Entree_Production') THEN msf.quantite " +
           "  WHEN ms.type_mouvement IN ('Sortie_Transfert', 'Vente_Client', 'Perte') THEN (msf.quantite * -1) " +
           "  ELSE 0 " +
           "END) as quantite_calculee " + // On calcule la somme
           "FROM MvtStockFille msf " +
           "JOIN MvtStock ms ON msf.idMvtStock = ms.id " +
           "WHERE ms.idptdevente = :codePointDeVente " +
           "AND ms.dateMvt <= :date " +
           "AND (:idProduit IS NULL OR msf.idproduit = :idProduit) " +
           "GROUP BY msf.idproduit", nativeQuery = true)
    public List<Object[]> calculerStockDisponibleRaw(
        @Param("codePointDeVente") String codePointDeVente, 
        @Param("date") LocalDateTime date,
        @Param("idProduit") Integer idProduit
    );
}
