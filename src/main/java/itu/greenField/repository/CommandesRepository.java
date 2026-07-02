package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Commandes;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
}
