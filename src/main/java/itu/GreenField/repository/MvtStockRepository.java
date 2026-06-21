package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.MvtStock;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
}
