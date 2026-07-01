package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.PanierFille;

public interface PanierFilleRepository extends JpaRepository<PanierFille, Integer> {
    List<PanierFille> findByPanier_Id(Integer idPanier);

    PanierFille findByPanier_IdAndProduit_Id(Integer idPanier, Integer idProduit);

    void deleteByPanier_Id(Integer idPanier);
}
