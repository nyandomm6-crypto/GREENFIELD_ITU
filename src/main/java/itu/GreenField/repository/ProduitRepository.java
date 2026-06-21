package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
}
