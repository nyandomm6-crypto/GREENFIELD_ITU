package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Employes;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {

    public Employes getById(Integer id);

}
