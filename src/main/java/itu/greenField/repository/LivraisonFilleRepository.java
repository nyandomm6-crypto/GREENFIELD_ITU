package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Commandes;
import itu.greenField.model.LivraisonFille;

import java.util.List;

public interface LivraisonFilleRepository extends JpaRepository<LivraisonFille, Integer> {
    List<LivraisonFille> findByCommande(Commandes commande);
}
