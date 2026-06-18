package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.DetailsCommande;

public interface DetailsCommandeRepository extends JpaRepository<DetailsCommande, Integer> {
}
