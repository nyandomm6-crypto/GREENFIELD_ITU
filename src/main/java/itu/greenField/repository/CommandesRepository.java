package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import itu.greenfield.model.Commandes;

import java.util.List;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    @Query(value = "SELECT * FROM commandes c " +
            "WHERE c.statutactuel != 2 " +
            "AND c.statutactuel != 4", nativeQuery = true)
    public List<Commandes> findDispoCommandes();

    @Query(value = "SELECT * FROM commandes c", nativeQuery = true)
    public Page<Commandes> findAllPaginated(Pageable pageable);
}
