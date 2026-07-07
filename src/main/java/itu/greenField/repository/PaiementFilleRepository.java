package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.PaiementFille;
import itu.greenField.model.TypePayement;

public interface PaiementFilleRepository extends JpaRepository<PaiementFille, Integer> {
    List<PaiementFille> findByPaiementId(Integer paiementId);

    List<PaiementFille> findByPaiementIdAndTypePayement(Integer paiementId, TypePayement typePayement);
}
