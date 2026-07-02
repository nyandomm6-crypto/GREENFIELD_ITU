package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.DemandeStock;

public interface DemandeStockRepository extends JpaRepository<DemandeStock, Integer> {
}
