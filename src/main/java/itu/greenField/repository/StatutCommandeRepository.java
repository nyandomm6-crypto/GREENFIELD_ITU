package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itu.greenField.model.StatutCommande;

@Repository
public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Integer> {

    Optional<StatutCommande> findByNom(String nom);

    boolean existsByNom(String nom);

    public Optional<StatutCommande> findFirstByNom(String nom);

}