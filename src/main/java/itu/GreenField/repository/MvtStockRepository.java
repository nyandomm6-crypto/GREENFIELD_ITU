package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.MvtStock;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.TypeMvt;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    List<MvtStock> findByPointDeVente(PointDeVente pointDeVente);
    List<MvtStock> findByPointDeVenteAndTypeMouvement(PointDeVente pointDeVente, TypeMvt typeMouvement);
}
