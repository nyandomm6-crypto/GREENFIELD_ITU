package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.CategorieProduit;

public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, Integer> {

    @Query("SELECT c FROM CategorieProduit c WHERE " +
            "(:motCle IS NULL OR LOWER(c.libelle) LIKE :motClePattern)")
    Page<CategorieProduit> search(@Param("motCle") String motCle,
            @Param("motClePattern") String motClePattern,
            Pageable pageable);

    Optional<CategorieProduit> findFirstByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}
