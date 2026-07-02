package itu.greenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.PointDeVente;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Integer> {
    Optional<PointDeVente> findFirstByCodeIgnoreCaseOrNomIgnoreCase(String code, String nom);
}
