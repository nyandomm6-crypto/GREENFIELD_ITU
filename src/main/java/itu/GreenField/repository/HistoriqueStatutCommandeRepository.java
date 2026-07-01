package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.HistoriqueStatutCommande;
import itu.GreenField.model.Commandes;

import java.util.Optional;
import java.util.List;


public interface HistoriqueStatutCommandeRepository extends JpaRepository<HistoriqueStatutCommande, Integer>{
    public Optional<HistoriqueStatutCommande> findFirstByCommandeOrderByDatechangementDesc(Commandes commande);


}
