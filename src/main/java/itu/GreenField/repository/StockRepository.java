package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.GreenField.model.MvtStock;

public interface StockRepository extends JpaRepository<MvtStock, Long> {

    @Query(value = """
            SELECT m.idptdevente AS codePointDeVente,
                   COALESCE(SUM(
                       CASE
                           WHEN m.type_mouvement IN ('Entree_Production', 'Entree_Boutique') THEN mf.quantite
                           WHEN m.type_mouvement IN ('Sortie_Transfert', 'Vente_Client', 'Perte') THEN -mf.quantite
                           ELSE 0
                       END
                   ), 0) AS stockTotal
            FROM mvtstock m
            JOIN mvtstockfille mf ON mf.idmvtstock = m.id
            WHERE mf.idproduit = :idProduit
              AND m.idptdevente <> :codePointDeVenteExclu
            GROUP BY m.idptdevente
            ORDER BY stockTotal DESC
            """, nativeQuery = true)
    List<PointDeVenteStockProjection> trouverStockParPointDeVentePourProduit(
            @Param("idProduit") Integer idProduit,
            @Param("codePointDeVenteExclu") String codePointDeVenteExclu);

    @Query(value = """
            SELECT m.idptdevente AS codePointDeVente,
                   COALESCE(SUM(
                       CASE
                           WHEN m.type_mouvement IN ('Entree_Production', 'Entree_Boutique') THEN mf.quantite
                           WHEN m.type_mouvement IN ('Sortie_Transfert', 'Vente_Client', 'Perte') THEN -mf.quantite
                           ELSE 0
                       END
                   ), 0) AS stockTotal
            FROM mvtstock m
            JOIN mvtstockfille mf ON mf.idmvtstock = m.id
            WHERE mf.idproduit IN (:idsProduits)
              AND m.idptdevente <> :codePointDeVenteExclu
            GROUP BY m.idptdevente
            ORDER BY stockTotal DESC
            """, nativeQuery = true)
    List<PointDeVenteStockProjection> trouverStockParPointDeVentePourProduits(
            @Param("idsProduits") List<Integer> idsProduits,
            @Param("codePointDeVenteExclu") String codePointDeVenteExclu);

    @Query(value = """
            SELECT COALESCE(SUM(
                       CASE
                           WHEN m.type_mouvement IN ('Entree_Production', 'Entree_Boutique') THEN mf.quantite
                           WHEN m.type_mouvement IN ('Sortie_Transfert', 'Vente_Client', 'Perte') THEN -mf.quantite
                           ELSE 0
                       END
                   ), 0)
            FROM mvtstock m
            JOIN mvtstockfille mf ON mf.idmvtstock = m.id
            WHERE mf.idproduit = :idProduit
              AND m.idptdevente = :codePointDeVente
            """, nativeQuery = true)
    Integer trouverStockDisponible(
            @Param("idProduit") Integer idProduit,
            @Param("codePointDeVente") String codePointDeVente);
}