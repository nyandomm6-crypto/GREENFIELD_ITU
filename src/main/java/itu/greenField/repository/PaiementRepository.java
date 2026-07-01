package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
}
