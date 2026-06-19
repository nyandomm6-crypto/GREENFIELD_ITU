package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.CategorieProduit;

public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, Integer> {
}
