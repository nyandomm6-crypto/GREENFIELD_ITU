package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.DemandeStock;

public interface DemandeStockRepository extends JpaRepository<DemandeStock, Integer> {
}
