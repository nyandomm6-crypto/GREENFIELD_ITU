package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
}
