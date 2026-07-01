package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
}
