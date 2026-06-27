package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    List<Produit> findByCategorie_Id(Integer idCategorie);

    List<Produit> findByNomContainingIgnoreCase(String motCle);

    List<Produit> findByCategorie_IdAndNomContainingIgnoreCase(Integer idCategorie, String motCle);
}
