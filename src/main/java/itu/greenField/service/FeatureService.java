 package itu.greenField.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Feature;
import itu.greenField.repository.FeatureRepository;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> findAll() {
        seedDefaultFeaturesIfEmpty();
        return featureRepository.findBySectionOrderByIdAsc("features");
    }

    public List<Feature> findStats() {
        seedDefaultFeaturesIfEmpty();
        return featureRepository.findBySectionOrderByIdAsc("stats");
    }

    public Feature findById(Long id) {
        return featureRepository.findById(id).orElse(null);
    }

    @Transactional
    public Feature save(Feature feature) {
        if (feature.getSection() == null || feature.getSection().isEmpty()) {
            feature.setSection("features");
        }
        return featureRepository.save(feature);
    }

    @Transactional
    public void deleteById(Long id) {
        featureRepository.deleteById(id);
    }

    @Transactional
    public void seedDefaultFeaturesIfEmpty() {
        if (featureRepository.count() > 0) {
            return;
        }

        List<Feature> features = new ArrayList<>();
        features.add(new Feature("fas fa-car-side", "Livraison gratuite", "Gratuite dès 300 000 Ar d'achat", "features"));
        features.add(new Feature("fas fa-user-shield", "Paiement sécurisé", "Paiement 100% sécurisé", "features"));
        features.add(new Feature("fas fa-exchange-alt", "Retours sous 30 jours", "Garantie satisfait ou remboursé", "features"));
        features.add(new Feature("fa fa-phone-alt", "Support 24/7", "Une assistance toujours rapide", "features"));

        features.add(new Feature("fa fa-users", "Clients satisfaits", "1963", "stats"));
        features.add(new Feature("fa fa-star", "Qualité de service", "99%", "stats"));
        features.add(new Feature("fa fa-certificate", "Certificats qualité", "33", "stats"));
        features.add(new Feature("fa fa-box", "Produits disponibles", "789", "stats"));

        featureRepository.saveAll(features);
    }
}
