package itu.GreenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.GreenField.model.ProvinceLivraison;
import itu.GreenField.repository.ProvinceLivraisonRepository;

@Service
public class ProvinceLivraisonService {
    private final ProvinceLivraisonRepository provinceLivraisonRepository;

    public ProvinceLivraisonService(ProvinceLivraisonRepository provinceLivraisonRepository) {
        this.provinceLivraisonRepository = provinceLivraisonRepository;
    }

    public List<ProvinceLivraison> getAllProvinces() {
        return provinceLivraisonRepository.findAll();
    }

    public ProvinceLivraison getProvinceById(Integer id) {
        return provinceLivraisonRepository.findById(id)
                .orElse(null);
    }
}
