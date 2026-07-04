package itu.greenfield.repository;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;
import itu.greenfield.model.Commandes;
import itu.greenfield.model.TypeFlux;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StatistiqueRepository extends JpaRepository<Commandes, Integer> {

    // --- FRONT-OFFICE ---

    // Top 5 produits les plus vendus avec filtres de date (pour le front ou back)
    @Query(value = "SELECT new itu.greenfield.dto.ProduitStatDto(p.id, p.nom, SUM(dc.quantite)) " +
                   "FROM DetailsCommande dc JOIN dc.commande c JOIN dc.produit p " +
                   "WHERE (:dateDebut IS NULL OR c.datecommande >= :dateDebut) " +
                   "AND (:dateFin IS NULL OR c.datecommande <= :dateFin) " +
                   "GROUP BY p.id, p.nom " +
                   "ORDER BY SUM(dc.quantite) DESC LIMIT 5")
    List<ProduitStatDto> findTop5ProduitsPlusVendus(
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    // Nouveaux produits (basé sur l'ID décroissant)
    @Query(value = "SELECT p FROM Produit p ORDER BY p.id DESC LIMIT 5")
    List<Produit> findNouveauxProduits();


    // --- BACK-OFFICE ---

    // Chiffre d'affaires d'une catégorie (ex: 'Fromage') avec filtres de date
    @Query(value = "SELECT COALESCE(SUM(dc.quantite * dc.puAuMomentAchat), 0) " +
                   "FROM DetailsCommande dc " +
                   "JOIN dc.commande c " +
                   "JOIN dc.produit p " +
                   "JOIN p.categorie cat " +
                   "WHERE LOWER(cat.libelle) LIKE LOWER(CONCAT('%', :categorie, '%')) " +
                   "AND c.statutCommande = 'Paye' " +
                   "AND (:dateDebut IS NULL OR c.datecommande >= :dateDebut) " +
                   "AND (:dateFin IS NULL OR c.datecommande <= :dateFin)")
    Double getChiffreAffairesParCategorie(
            @Param("categorie") String categorie,
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    // Somme des mouvements de trésorerie par type (Entrée ou Dépense) avec filtres de date
    @Query(value = "SELECT COALESCE(SUM(t.montant), 0) FROM Tresorerie t " +
                   "WHERE t.typeMouvement = :type " +
                   "AND (:dateDebut IS NULL OR t.dateOperation >= :dateDebut) " +
                   "AND (:dateFin IS NULL OR t.dateOperation <= :dateFin)")
    Double getSommeTresorerie(
            @Param("type") TypeFlux type,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    // Meilleure vente (courbe) : Evolution des ventes avec filtres par produit et par date
    @Query(value = "SELECT new itu.greenfield.dto.EvolutionVenteDto(CAST(c.datecommande AS localdate), SUM(dc.quantite * dc.puAuMomentAchat)) " +
                   "FROM DetailsCommande dc JOIN dc.commande c JOIN dc.produit p " +
                   "WHERE c.statutCommande = 'Paye' " +
                   "AND (:idproduit IS NULL OR p.id = :idproduit) " +
                   "AND (:dateDebut IS NULL OR c.datecommande >= :dateDebut) " +
                   "AND (:dateFin IS NULL OR c.datecommande <= :dateFin) " +
                   "GROUP BY CAST(c.datecommande AS localdate) " +
                   "ORDER BY CAST(c.datecommande AS localdate) ASC")
    List<EvolutionVenteDto> findEvolutionDesVentes(
            @Param("idproduit") Integer idproduit,
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    // Top 5 meilleurs clients en ligne avec filtres de date
    @Query(value = "SELECT new itu.greenfield.dto.ClientStatDto(cl.id, cl.nom, cl.prenom, SUM(c.totalGeneral)) " +
                   "FROM Commandes c JOIN c.client cl " +
                   "WHERE c.statutCommande = 'Paye' " +
                   "AND c.typeCommande = itu.greenfield.model.TypeCommande.En_ligne " +
                   "AND (:dateDebut IS NULL OR c.datecommande >= :dateDebut) " +
                   "AND (:dateFin IS NULL OR c.datecommande <= :dateFin) " +
                   "GROUP BY cl.id, cl.nom, cl.prenom " +
                   "ORDER BY SUM(c.totalGeneral) DESC LIMIT 5")
    List<ClientStatDto> findTop5MeilleursClients(
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);
}