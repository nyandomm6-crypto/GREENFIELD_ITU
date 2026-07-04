package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Employes;
import itu.greenField.model.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    public Livraison getById(Integer id);

    List<Livraison> findByLivreur(Employes livreur);
}
