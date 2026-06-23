package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Employes;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {
    public Employes getById(Integer id);

}
