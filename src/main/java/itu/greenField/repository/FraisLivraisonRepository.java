package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.FraisLivraison;

import java.util.Optional;

public interface FraisLivraisonRepository extends JpaRepository<FraisLivraison, Integer> {
    public Optional<FraisLivraison> findByProvinceLivraisonId(Integer provinceId);

    public Optional<FraisLivraison> findFirstByProvinceLivraisonIdAndPoidsReferenceGreaterThanOrderByPoidsReferenceAsc(Integer provinceId, Double poidsReference);
}
