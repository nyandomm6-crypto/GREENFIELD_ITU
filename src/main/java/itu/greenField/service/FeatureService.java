package itu.greenField.service;

import itu.greenField.model.Feature;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeatureService {

    public List<Feature> findAll() {

        return List.of(
                new Feature("fas fa-car-side", "Free Shipping", "Free on order over $300"),
                new Feature("fas fa-user-shield", "Security Payment", "100% security payment"),
                new Feature("fas fa-exchange-alt", "30 Day Return", "30 day money guarantee"),
                new Feature("fa fa-phone-alt", "24/7 Support", "Support every time fast"));
    }

    public List<Feature> findStats() {

        return List.of(
                new Feature("fa fa-users", "Clients satisfaits", "1963"),
                new Feature("fa fa-star", "Qualité de service", "99%"),
                new Feature("fa fa-certificate", "Certificats qualité", "33"),
                new Feature("fa fa-box", "Produits disponibles", "789"));
    }

}