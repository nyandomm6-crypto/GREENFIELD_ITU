package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.DetailsCommande;

public interface DetailsCommandeRepository extends JpaRepository<DetailsCommande, Integer> {
}
