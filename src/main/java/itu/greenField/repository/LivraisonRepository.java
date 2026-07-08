package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    
}
