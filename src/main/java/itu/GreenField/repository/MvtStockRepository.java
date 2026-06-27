package itu.GreenField.repository;

import itu.GreenField.model.MvtStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    
    @Query("SELECT COALESCE(SUM(CASE " +
           "  WHEN ms.typeMouvement IN (itu.GreenField.model.TypeMvt.Entree_Production, itu.GreenField.model.TypeMvt.Entree_Boutique) THEN msf.quantite " +
           "  ELSE -msf.quantite END), 0) " +
           "FROM MvtStockFille msf JOIN msf.mvtStock ms " +
           "WHERE msf.produit.id = :idProduit AND (:ptDeVenteCode IS NULL OR ms.pointDeVente.code = :ptDeVenteCode)")
    Integer getStockByProduitAndOptionalPointDeVente(@Param("idProduit") Integer idProduit, @Param("ptDeVenteCode") String ptDeVenteCode);
}

