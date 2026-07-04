package itu.greenField.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.StatutCommande;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    public List<Commandes> findByClient(Client client);

    List<Commandes> findByStatutActuel(StatutCommande statutActuel);

    @Query(value = "SELECT * FROM commandes c " +
            "WHERE c.statutactuel != 2 " +
            "AND c.statutactuel != 4", nativeQuery = true)
    public List<Commandes> findDispoCommandes();

    @Query(value = "SELECT * FROM commandes c", nativeQuery = true)
    public Page<Commandes> findAllPaginated(Pageable pageable);
}
