package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.FraisLivraison;

import java.util.Optional;

public interface FraisLivraisonRepository extends JpaRepository<FraisLivraison, Integer> {
    public Optional<FraisLivraison> findByProvinceLivraisonId(Integer provinceId);

    public Optional<FraisLivraison> findFirstByProvinceLivraisonIdAndPoidsReferenceLessThanEqualOrderByPoidsReferenceDesc(
            Integer provinceId, Double poidsReference);

    @Query(value = "SELECT * FROM fraislivraison f "+
                    "WHERE f.poidsreference = (SELECT MAX(poidsreference) FROM fraislivraison WHERE idprovince = :provinceId) "+
                    "AND f.idprovince = :provinceId", nativeQuery = true)
    public Optional<FraisLivraison> findMaxPoidsReferenceByProvinceId(@Param("provinceId") Integer provinceId);
}
