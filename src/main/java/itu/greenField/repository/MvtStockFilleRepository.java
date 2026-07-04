package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.MvtStockFille;

public interface MvtStockFilleRepository extends JpaRepository<MvtStockFille, Integer> {
    @Query("""
            SELECT COALESCE(SUM(
                CASE
                    WHEN m.typeMouvement = itu.greenField.model.TypeMvt.Entree_Production THEN msf.quantite
                    WHEN m.typeMouvement = itu.greenField.model.TypeMvt.Entree_Boutique THEN msf.quantite
                    ELSE -msf.quantite
                END
            ), 0)
            FROM MvtStockFille msf
            JOIN msf.mvtStock m
            WHERE msf.produit.id = :idProduit
            """)
    Integer calculerStockProduit(@Param("idProduit") Integer idProduit);
}
