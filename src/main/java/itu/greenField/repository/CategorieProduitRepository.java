package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.CategorieProduit;

public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, Integer> {
}
