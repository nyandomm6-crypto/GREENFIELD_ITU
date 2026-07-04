package itu.greenField.repository;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;
import itu.greenField.model.Commandes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatistiqueRepository extends JpaRepository<Commandes, Integer> {

    // --- FRONT-OFFICE ---

    // Top 5 produits les plus vendus
    @Query(value = "SELECT new itu.greenField.dto.ProduitStatDto(p.id, p.nom, SUM(dc.quantite)) " +
            "FROM DetailsCommande dc JOIN dc.produit p " +
            "GROUP BY p.id, p.nom " +
            "ORDER BY SUM(dc.quantite) DESC LIMIT 5")
    List<ProduitStatDto> findTop5ProduitsPlusVendus();

    // Nouveaux produits (basé sur l'ID décroissant)
    @Query(value = "SELECT p FROM Produit p ORDER BY p.id DESC LIMIT 5")
    List<Produit> findNouveauxProduits();

    // --- BACK-OFFICE ---

    // Chiffre d'affaires / Bénéfice d'une catégorie (ex: 'Fromage')
    // Note : N'ayant pas de prix d'achat dans ta table Produit, on calcule le CA
    // généré par la catégorie.
    @Query(value = "SELECT COALESCE(SUM(dc.quantite * dc.puAuMomentAchat), 0) " +
            "FROM DetailsCommande dc " +
            "JOIN dc.produit p " +
            "JOIN p.categorie c " +
            "WHERE LOWER(c.libelle) LIKE LOWER(CONCAT('%', :categorie, '%'))")
    Double getChiffreAffairesParCategorie(@Param("categorie") String categorie);

    // Meilleure vente (courbe) : Evolution des ventes par jour
    @Query(value = "SELECT new itu.greenField.dto.EvolutionVenteDto(CAST(c.datecommande AS localdate), SUM(c.totalGeneral)) "
            +
            "FROM Commandes c " +
            "WHERE c.statutActuel.nom = 'Payée' " +
            "GROUP BY CAST(c.datecommande AS localdate) " +
            "ORDER BY CAST(c.datecommande AS localdate) ASC")
    List<EvolutionVenteDto> findEvolutionDesVentes();

    // Top 5 meilleurs clients en ligne
    @Query(value = "SELECT new itu.greenField.dto.ClientStatDto(cl.id, cl.nom, cl.prenom, SUM(c.totalGeneral)) " +
            "FROM Commandes c JOIN c.client cl " +
            "WHERE c.statutActuel.nom = 'Payée' " +
            "GROUP BY cl.id, cl.nom, cl.prenom " +
            "ORDER BY SUM(c.totalGeneral) DESC LIMIT 5")
    List<ClientStatDto> findTop5MeilleursClients();
}