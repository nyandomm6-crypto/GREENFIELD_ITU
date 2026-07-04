package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.PointDeVente;

import java.util.List;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {

    public Employes getById(Integer id);
    List<Employes> findByPointDeVente(PointDeVente pointDeVente);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCase(PointDeVente pointDeVente, String nom);
    List<Employes> findByPointDeVenteAndRole(PointDeVente pointDeVente, FRole role);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCaseAndRole(PointDeVente pointDeVente, String nom, FRole role);


}
