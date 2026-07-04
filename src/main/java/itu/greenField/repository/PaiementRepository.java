package itu.greenField.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.Commandes;
import itu.greenField.model.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    // Paiement findByCommande(Commandes commande);

    Optional<Paiement> findByCommande(Commandes commande);

    @Query("""
            SELECT COALESCE(SUM(pf.valeur),0)
            FROM PaiementFille pf
            WHERE pf.paiement.commande.id = :idCommande
            """)
    BigDecimal sommePaye(@Param("idCommande") Integer idCommande);
}
