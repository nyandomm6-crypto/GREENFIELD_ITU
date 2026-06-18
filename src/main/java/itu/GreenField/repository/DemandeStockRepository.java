package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.DemandeStock;

public interface DemandeStockRepository extends JpaRepository<DemandeStock, Integer> {
}
