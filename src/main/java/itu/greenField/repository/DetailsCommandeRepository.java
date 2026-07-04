package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.DetailsCommande;

public interface DetailsCommandeRepository extends JpaRepository<DetailsCommande, Integer> {
}
