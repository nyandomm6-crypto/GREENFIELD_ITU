package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {
    public Vehicule getById(Integer id);
}
