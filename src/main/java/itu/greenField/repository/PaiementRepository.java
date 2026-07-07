package itu.greenField.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Paiement;
import itu.greenField.model.StatutPaiement;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    Optional<Paiement> findByCommandeId(Integer commandeId);

    List<Paiement> findByStatut(StatutPaiement statut);
}
