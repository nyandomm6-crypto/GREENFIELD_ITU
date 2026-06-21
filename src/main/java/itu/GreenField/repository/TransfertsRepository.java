package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Transferts;

public interface TransfertsRepository extends JpaRepository<Transferts, Integer> {
}
