package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Panier;

public interface PanierRepository extends JpaRepository<Panier, Integer> {
    Panier findByTokenSession(String tokenSession);

    Panier findByClient_Id(Integer idClient);
}
