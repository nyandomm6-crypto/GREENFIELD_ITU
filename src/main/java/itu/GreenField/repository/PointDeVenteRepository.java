package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.PointDeVente;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Integer> {
    List<PointDeVente> findByCodeContainingIgnoreCase(String code);
}
