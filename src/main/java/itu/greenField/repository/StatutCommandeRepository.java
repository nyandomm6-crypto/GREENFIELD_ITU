package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.StatutCommande;

public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Integer> {
    public Optional<StatutCommande> findFirstByNom(String nom);
}
