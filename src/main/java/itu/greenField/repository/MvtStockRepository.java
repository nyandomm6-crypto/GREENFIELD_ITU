package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.MvtStock;
import itu.greenfield.model.PointDeVente;
import itu.greenfield.model.TypeMvt;

import java.util.List;

public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    List<MvtStock> findByPointDeVente(PointDeVente pointDeVente);
    List<MvtStock> findByPointDeVenteAndTypeMouvement(PointDeVente pointDeVente, TypeMvt typeMouvement);
}
