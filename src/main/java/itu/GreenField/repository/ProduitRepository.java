package itu.GreenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Produit;


public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    Optional<Produit> findByMatricule(String matricule);
}
