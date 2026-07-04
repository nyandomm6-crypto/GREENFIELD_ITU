package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.ProvinceLivraison;

public interface ProvinceLivraisonRepository extends JpaRepository<ProvinceLivraison, Integer> {
    public Optional<ProvinceLivraison> findFirstByNomContainingIgnoreCase(String nom);

}
