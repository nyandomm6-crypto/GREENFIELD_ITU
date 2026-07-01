package itu.GreenField.repository;

import itu.GreenField.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    
    @Query("SELECT p FROM Produit p WHERE " +
           "(:idCategorie IS NULL OR p.categorie.id = :idCategorie) AND " +
           "(:motCle IS NULL OR LOWER(p.nom) LIKE :motClePattern " +
           "OR LOWER(p.matricule) LIKE :motClePattern)")
    List<Produit> searchProduits(@Param("idCategorie") Integer idCategorie, 
                                 @Param("motCle") String motCle,
                                 @Param("motClePattern") String motClePattern);

    boolean existsByMatricule(String matricule);
    
    boolean existsByMatriculeAndIdNot(String matricule, Integer id);
}

