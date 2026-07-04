package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.StatutCommande;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    public List<Commandes> findByClient(Client client);

    List<Commandes> findByStatutActuel(StatutCommande statutActuel);
}
