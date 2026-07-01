package itu.GreenField.service;

import org.springframework.stereotype.Service;

import itu.GreenField.model.FraisLivraison;
import itu.GreenField.repository.FraisLivraisonRepository;

@Service
public class FraisLivraisonService {
    private final FraisLivraisonRepository fraisLivraisonRepository;

    public FraisLivraisonService(FraisLivraisonRepository fraisLivraisonRepository) {
        this.fraisLivraisonRepository = fraisLivraisonRepository;
    }

    public FraisLivraison calculateFraisLivraison(Integer provinceId, Double poids) {
        FraisLivraison fraisLivraison = fraisLivraisonRepository
                .findFirstByProvinceLivraisonIdAndPoidsReferenceGreaterThanOrderByPoidsReferenceAsc(provinceId, poids)
                .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé pour le poids : " + poids));
        return fraisLivraison;
    }
}
