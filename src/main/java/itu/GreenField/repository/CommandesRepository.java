package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Commandes;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
}
