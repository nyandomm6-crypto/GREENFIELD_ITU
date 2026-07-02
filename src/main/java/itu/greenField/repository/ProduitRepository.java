package itu.greenField.repository;

import java.lang.StackWalker.Option;
import java.nio.file.OpenOption;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Produit;

import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    public Optional<Produit> findFirstByNom(String nom);
}
