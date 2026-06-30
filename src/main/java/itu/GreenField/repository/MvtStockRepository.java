package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.MvtStock;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.model.TypeMvt;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    List<MvtStock> findByPointDeVente(PointDeVente pointDeVente);
    List<MvtStock> findByPointDeVenteAndTypeMouvement(PointDeVente pointDeVente, TypeMvt typeMouvement);
}
