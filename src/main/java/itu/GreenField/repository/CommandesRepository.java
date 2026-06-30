package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import itu.greenfield.model.Commandes;
import java.util.List;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    @Query(value = "SELECT * FROM commandes c " +
            "WHERE c.statutcommande != 'Paye' " +
            "AND c.statutcommande != 'Annule'", nativeQuery = true)
    public List<Commandes> findDispoCommandes();

    @Query(value = "SELECT * FROM commandes c", nativeQuery = true)
    public Page<Commandes> findAllPaginated(Pageable pageable);

    @Query(value = "SELECT * FROM commandes c WHERE 1 = 1" +
            "AND :statut IS NULL OR :statut = '' OR c.statutcommande = :statut", nativeQuery = true)
    public Page<Commandes> findFilteredAndPaginated(@Param("statut") String statut, Pageable pageable);
}
