package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.PointDeVente;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {
    List<Employes> findByPointDeVente(PointDeVente pointDeVente);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCase(PointDeVente pointDeVente, String nom);
    List<Employes> findByPointDeVenteAndRole(PointDeVente pointDeVente, FRole role);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCaseAndRole(PointDeVente pointDeVente, String nom, FRole role);
}
