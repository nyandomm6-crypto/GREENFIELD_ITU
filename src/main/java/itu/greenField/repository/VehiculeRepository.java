package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {
    public Vehicule getById(Integer id);
}
