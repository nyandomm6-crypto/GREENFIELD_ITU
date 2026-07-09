package itu.greenField.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
        Optional<Produit> findByMatricule(String matricule);

        Page<Produit> findByCategorie_Id(Integer idCategorie, Pageable pageable);

        Page<Produit> findByNomContainingIgnoreCase(String motCle, Pageable pageable);

        Page<Produit> findByCategorie_IdAndNomContainingIgnoreCase(Integer idCategorie, String motCle,
                        Pageable pageable);

        public Optional<Produit> findFirstByNom(String nom);

        @Query("SELECT p FROM Produit p WHERE " +
                        "(:idCategorie IS NULL OR p.categorie.id = :idCategorie) AND " +
                        "(:motCle IS NULL OR LOWER(p.nom) LIKE :motClePattern " +
                        "OR LOWER(p.matricule) LIKE :motClePattern)")
        List<Produit> searchProduits(@Param("idCategorie") Integer idCategorie,
                        @Param("motCle") String motCle,
                        @Param("motClePattern") String motClePattern);

        @Query("SELECT p FROM Produit p WHERE " +
                        "(:idCategorie IS NULL OR p.categorie.id = :idCategorie) AND " +
                        "(:motCle IS NULL OR LOWER(p.nom) LIKE :motClePattern " +
                        "OR LOWER(p.matricule) LIKE :motClePattern)")
        Page<Produit> searchProduits(@Param("idCategorie") Integer idCategorie,
                        @Param("motCle") String motCle,
                        @Param("motClePattern") String motClePattern,
                        Pageable pageable);

        boolean existsByMatricule(String matricule);

        boolean existsByMatriculeAndIdNot(String matricule, Integer id);

        // 5 produits les plus vendus (somme des quantités commandées décroissante)
        @Query("SELECT p FROM Produit p JOIN p.detailsCommande dc " +
                        "GROUP BY p ORDER BY SUM(dc.quantite) DESC")
        List<Produit> findBestSellers(Pageable pageable);

        // Nouveaux produits (les plus récents par id décroissant)
        List<Produit> findTop10ByOrderByIdDesc();

}
