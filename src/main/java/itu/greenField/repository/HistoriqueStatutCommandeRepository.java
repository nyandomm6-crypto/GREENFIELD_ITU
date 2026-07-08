package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Commandes;
import itu.greenfield.model.HistoriqueStatutCommande;

import java.util.Optional;


public interface HistoriqueStatutCommandeRepository extends JpaRepository<HistoriqueStatutCommande, Integer>{
    public Optional<HistoriqueStatutCommande> findFirstByCommandeOrderByDatechangementDesc(Commandes commande);


}
