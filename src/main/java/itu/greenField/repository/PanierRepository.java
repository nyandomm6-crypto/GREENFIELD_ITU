package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Panier;

public interface PanierRepository extends JpaRepository<Panier, Integer> {
    Panier findByTokenSession(String tokenSession);

    Panier findByClient_Id(Integer idClient);
}
