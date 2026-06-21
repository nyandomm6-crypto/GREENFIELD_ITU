package itu.greenfield.repository;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;
import itu.greenfield.model.Commandes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatistiqueRepository extends JpaRepository<Commandes, Integer> {

    // --- FRONT-OFFICE ---

    // Top 5 produits les plus vendus
    @Query(value = "SELECT new com.greenfield.dto.ProduitStatDto(p.id, p.nom, SUM(dc.quantite)) " +
                   "FROM DetailsCommande dc JOIN dc.idproduit p " +
                   "GROUP BY p.id, p.nom " +
                   "ORDER BY SUM(dc.quantite) DESC LIMIT 5")
    List<ProduitStatDto> findTop5ProduitsPlusVendus();

    // Nouveaux produits (basé sur l'ID décroissant)
    @Query(value = "SELECT p FROM Produit p ORDER BY p.id DESC LIMIT 5")
    List<Produit> findNouveauxProduits();


    // --- BACK-OFFICE ---

    // Chiffre d'affaires / Bénéfice d'une catégorie (ex: 'Fromage')
    // Note : N'ayant pas de prix d'achat dans ta table Produit, on calcule le CA généré par la catégorie.
    @Query(value = "SELECT COALESCE(SUM(dc.quantite * dc.pu_au_moment_achat), 0) " +
                   "FROM DetailsCommande dc " +
                   "JOIN dc.idproduit p " +
                   "JOIN p.idcategorie c " +
                   "WHERE LOWER(c.libelle) LIKE LOWER(CONCAT('%', :categorie, '%'))")
    Double getChiffreAffairesParCategorie(@Param("categorie") String categorie);

    // Meilleure vente (courbe) : Evolution des ventes par jour
    @Query(value = "SELECT new com.greenfield.dto.EvolutionVenteDto(CAST(c.datecommande AS localdate), SUM(c.total_general)) " +
                   "FROM Commandes c " +
                   "WHERE c.statutCommande = 'Paye' " +
                   "GROUP BY CAST(c.datecommande AS localdate) " +
                   "ORDER BY CAST(c.datecommande AS localdate) ASC")
    List<EvolutionVenteDto> findEvolutionDesVentes();

    // Top 5 meilleurs clients en ligne
    @Query(value = "SELECT new com.greenfield.dto.ClientStatDto(cl.id, cl.nom, cl.prenom, SUM(c.total_general)) " +
                   "FROM Commandes c JOIN c.idclient cl " +
                   "WHERE c.statutCommande = 'Paye' " +
                   "GROUP BY cl.id, cl.nom, cl.prenom " +
                   "ORDER BY SUM(c.total_general) DESC LIMIT 5")
    List<ClientStatDto> findTop5MeilleursClients();
}