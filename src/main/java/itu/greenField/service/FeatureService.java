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
        features.add(new Feature("fas fa-car-side", "Free Shipping", "Free on order over $300", "features"));
        features.add(new Feature("fas fa-user-shield", "Security Payment", "100% security payment", "features"));
        features.add(new Feature("fas fa-exchange-alt", "30 Day Return", "30 day money guarantee", "features"));
        features.add(new Feature("fa fa-phone-alt", "24/7 Support", "Support every time fast", "features"));

        features.add(new Feature("fa fa-users", "Clients satisfaits", "1963", "stats"));
        features.add(new Feature("fa fa-star", "Qualité de service", "99%", "stats"));
        features.add(new Feature("fa fa-certificate", "Certificats qualité", "33", "stats"));
        features.add(new Feature("fa fa-box", "Produits disponibles", "789", "stats"));

        featureRepository.saveAll(features);
    }
}
