package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.MvtStock;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.TypeMvt;

import java.util.List;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    List<MvtStock> findByPointDeVente(PointDeVente pointDeVente);
    List<MvtStock> findByPointDeVenteAndTypeMouvement(PointDeVente pointDeVente, TypeMvt typeMouvement);
}
