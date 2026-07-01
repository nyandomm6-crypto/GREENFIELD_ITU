package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.MvtStock;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
}
