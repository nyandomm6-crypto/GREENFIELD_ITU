package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.MvtStock;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.TypeMvt;

import java.util.List;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
        List<MvtStock> findByPointDeVente(PointDeVente pointDeVente);

        List<MvtStock> findByPointDeVenteAndTypeMouvement(PointDeVente pointDeVente, TypeMvt typeMouvement);

        @Query("SELECT COALESCE(SUM(CASE " +
                        "  WHEN ms.typeMouvement IN (itu.greenField.model.TypeMvt.Entree_Production, itu.greenField.model.TypeMvt.Entree_Boutique) THEN msf.quantite "
                        +
                        "  ELSE -msf.quantite END), 0) " +
                        "FROM MvtStockFille msf JOIN msf.mvtStock ms LEFT JOIN ms.pointDeVente pdv " +
                        "WHERE msf.produit.id = :idProduit AND (:ptDeVenteCode IS NULL OR pdv.code = :ptDeVenteCode)")
        Integer getStockByProduitAndOptionalPointDeVente(@Param("idProduit") Integer idProduit,
                        @Param("ptDeVenteCode") String ptDeVenteCode);
}
