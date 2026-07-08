package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
}
