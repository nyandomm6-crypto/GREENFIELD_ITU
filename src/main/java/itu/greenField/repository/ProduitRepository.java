package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    Optional<Produit> findByMatricule(String matricule);
    public Optional<Produit> findFirstByNom(String nom);

}
