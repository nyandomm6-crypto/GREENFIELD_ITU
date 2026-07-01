package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    public Livraison getById(Integer id);
}
