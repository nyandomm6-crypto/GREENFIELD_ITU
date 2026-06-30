package itu.GreenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Employes;
import itu.GreenField.model.FRole;
import itu.GreenField.model.PointDeVente;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {
    List<Employes> findByPointDeVente(PointDeVente pointDeVente);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCase(PointDeVente pointDeVente, String nom);
    List<Employes> findByPointDeVenteAndRole(PointDeVente pointDeVente, FRole role);
    List<Employes> findByPointDeVenteAndNomContainingIgnoreCaseAndRole(PointDeVente pointDeVente, String nom, FRole role);
}
