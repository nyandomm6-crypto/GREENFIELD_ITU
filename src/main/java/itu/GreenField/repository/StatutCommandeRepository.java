package itu.GreenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.StatutCommande;

public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Integer> {
    public Optional<StatutCommande> findFirstByNom(String nom);
}
