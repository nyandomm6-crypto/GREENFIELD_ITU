package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.AvisProduit;
import itu.greenField.model.Produit;

public interface AvisProduitRepository extends JpaRepository<AvisProduit, Integer> {
    List<AvisProduit> findByProduitOrderByDateCreationDesc(Produit produit);
}
