--Pour obtenir la quantité exacte de chaque produit dans chaque boutique.

CREATE OR REPLACE VIEW V_Stock_Actuel AS
SELECT 
    pt.code AS idptdevente,
    pt.nom AS nom_point_de_vente,
    p.id AS idproduit,
    p.nom AS nom_produit,
    COALESCE(SUM(
        CASE 
            -- Entrées en stock
            WHEN m.type_mouvement IN ('Entree_Production', 'Entree_Boutique') THEN mf.quantite
            -- Sorties de stock
            WHEN m.type_mouvement IN ('Sortie_Transfert', 'Vente_Client', 'Perte') THEN -mf.quantite
            ELSE 0
        END
    ), 0) + 
    -- Prise en compte des transferts inter-boutiques validés (Termine)
    COALESCE((
        SELECT SUM(tf.quantite)
        FROM Transferts t
        JOIN TransfertsFille tf ON t.id = tf.idTransfert
        WHERE t.idPointDeVenteCible = pt.code 
          AND t.statut_transfert = 'Termine' 
          AND tf.idproduit = p.id
    ), 0) -
    COALESCE((
        SELECT SUM(tf.quantite)
        FROM Transferts t
        JOIN TransfertsFille tf ON t.id = tf.idTransfert
        WHERE t.idPointDeVente = pt.code 
          AND t.statut_transfert = 'Termine' 
          AND tf.idproduit = p.id
    ), 0) AS stock_net
FROM PointDeVente pt
CROSS JOIN Produit p
LEFT JOIN MvtStock m ON m.idptdevente = pt.code
LEFT JOIN MvtStockFille mf ON mf.idMvtStock = m.id AND mf.idproduit = p.id
GROUP BY pt.code, pt.nom, p.id, p.nom;