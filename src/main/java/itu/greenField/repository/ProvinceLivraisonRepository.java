package itu.greenfield.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.ProvinceLivraison;

public interface ProvinceLivraisonRepository extends JpaRepository<ProvinceLivraison, Integer> {
    public Optional<ProvinceLivraison> findFirstByNomContainingIgnoreCase(String nom);

}
