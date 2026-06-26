package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import itu.GreenField.model.Commandes;
import java.util.List;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    @Query(value = "SELECT * FROM commandes c " +
            "WHERE c.statutcommande != 'Paye' " +
            "AND c.statutcommande != 'Annule'", nativeQuery = true)
    List<Commandes> findDispoCommandes();
}
