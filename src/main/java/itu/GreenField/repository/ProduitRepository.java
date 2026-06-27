package itu.GreenField.repository;

import itu.GreenField.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    
    @Query("SELECT p FROM Produit p WHERE " +
           "(:idCategorie IS NULL OR p.categorie.id = :idCategorie) AND " +
           "(:motCle IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "OR LOWER(p.matricule) LIKE LOWER(CONCAT('%', :motCle, '%')))")
    List<Produit> searchProduits(@Param("idCategorie") Integer idCategorie, @Param("motCle") String motCle);

    boolean existsByMatricule(String matricule);
    
    boolean existsByMatriculeAndIdNot(String matricule, Integer id);
}

