package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Commandes;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
}
