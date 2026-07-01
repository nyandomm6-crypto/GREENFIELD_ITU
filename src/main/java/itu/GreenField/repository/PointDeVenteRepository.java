package itu.GreenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.PointDeVente;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Integer> {
    Optional<PointDeVente> findByCode(String code);
}
