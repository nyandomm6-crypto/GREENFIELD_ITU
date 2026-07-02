package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.CategorieProduit;

public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, Integer> {
}
