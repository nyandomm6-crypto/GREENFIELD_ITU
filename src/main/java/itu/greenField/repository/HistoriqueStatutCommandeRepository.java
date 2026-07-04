package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Commandes;
import itu.greenField.model.HistoriqueStatutCommande;

import java.util.Optional;


public interface HistoriqueStatutCommandeRepository extends JpaRepository<HistoriqueStatutCommande, Integer>{
    public Optional<HistoriqueStatutCommande> findFirstByCommandeOrderByDatechangementDesc(Commandes commande);


}
