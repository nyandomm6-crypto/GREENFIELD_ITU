package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    
}
