package itu.greenField.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.PointDeVente;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Integer> {
    List<PointDeVente> findAllByOrderByNomAsc();

    Optional<PointDeVente> findByCode(String code);
}
