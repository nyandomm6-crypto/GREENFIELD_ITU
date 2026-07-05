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

    @Query("SELECT new itu.greenfield.dto.ProduitStatDto(p.id, p.nom, SUM(dc.quantite)) " +
           "FROM DetailsCommande dc JOIN dc.commande c JOIN dc.produit p " +
           "WHERE c.statutCommande = itu.greenfield.model.StatutCommande.Paye " +
           "AND c.datecommande >= :dateDebut AND c.datecommande <= :dateFin " +
           "GROUP BY p.id, p.nom " +
           "ORDER BY SUM(dc.quantite) DESC LIMIT 5")
    List<ProduitStatDto> findTop5ProduitsPlusVendus(
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    @Query("SELECT p FROM Produit p ORDER BY p.id DESC LIMIT 5")
    List<Produit> findNouveauxProduits();

    @Query("SELECT new itu.greenfield.dto.ProduitStatDto(p.id, p.nom, SUM(dc.quantite)) " +
           "FROM DetailsCommande dc JOIN dc.commande c JOIN dc.produit p " +
           "WHERE c.statutCommande = itu.greenfield.model.StatutCommande.Paye " +
           "AND c.datecommande >= :dateDebut AND c.datecommande <= :dateFin " +
           "GROUP BY p.id, p.nom " +
           "ORDER BY SUM(dc.quantite) DESC")
    List<ProduitStatDto> findHistoriqueVentesGlobal(
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    // --- BACK-OFFICE ---

    @Query("SELECT COALESCE(SUM(dc.quantite * dc.puAuMomentAchat), 0) " +
           "FROM DetailsCommande dc " +
           "JOIN dc.commande c " +
           "JOIN dc.produit p " +
           "JOIN p.categorie cat " +
           "WHERE LOWER(cat.libelle) LIKE LOWER(CONCAT('%', :categorie, '%')) " +
           "AND c.statutCommande = itu.greenfield.model.StatutCommande.Paye " +
           "AND c.datecommande >= :dateDebut AND c.datecommande <= :dateFin")
    Double getChiffreAffairesParCategorie(
            @Param("categorie") String categorie,
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Tresorerie t " +
           "WHERE t.typeMouvement = :type " +
           "AND t.dateOperation >= :dateDebut AND t.dateOperation <= :dateFin")
    Double getSommeTresorerie(
            @Param("type") TypeFlux type,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT new itu.greenfield.dto.EvolutionVenteDto(CAST(c.datecommande AS localdate), SUM(dc.quantite * dc.puAuMomentAchat)) " +
           "FROM DetailsCommande dc JOIN dc.commande c JOIN dc.produit p " +
           "WHERE c.statutCommande = itu.greenfield.model.StatutCommande.Paye " +
           "AND (:idproduit IS NULL OR p.id = :idproduit) " +
           "AND c.datecommande >= :dateDebut AND c.datecommande <= :dateFin " +
           "GROUP BY CAST(c.datecommande AS localdate) " +
           "ORDER BY CAST(c.datecommande AS localdate) ASC")
    List<EvolutionVenteDto> findEvolutionDesVentes(
            @Param("idproduit") Integer idproduit,
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);

    @Query("SELECT new itu.greenfield.dto.ClientStatDto(cl.id, cl.nom, cl.prenom, SUM(c.totalGeneral)) " +
           "FROM Commandes c JOIN c.client cl " +
           "WHERE c.statutCommande = itu.greenfield.model.StatutCommande.Paye " +
           "AND c.typeCommande = itu.greenfield.model.TypeCommande.En_ligne " +
           "AND c.datecommande >= :dateDebut AND c.datecommande <= :dateFin " +
           "GROUP BY cl.id, cl.nom, cl.prenom " +
           "ORDER BY SUM(c.totalGeneral) DESC LIMIT 5")
    List<ClientStatDto> findTop5MeilleursClients(
            @Param("dateDebut") Timestamp dateDebut,
            @Param("dateFin") Timestamp dateFin);
}
