package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {
    public Vehicule getById(Integer id);
}
