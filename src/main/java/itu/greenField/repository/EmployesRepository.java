package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Employes;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {

    public Employes getById(Integer id);

}
