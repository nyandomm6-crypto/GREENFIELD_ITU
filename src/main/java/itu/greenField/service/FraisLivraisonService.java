package itu.greenField.service;

import org.springframework.stereotype.Service;

import itu.greenField.model.FraisLivraison;
import itu.greenField.repository.FraisLivraisonRepository;

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
