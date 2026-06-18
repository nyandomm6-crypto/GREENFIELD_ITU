package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.MvtStock;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
}
