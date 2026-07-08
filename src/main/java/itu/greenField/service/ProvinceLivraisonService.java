package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.ProvinceLivraison;
import itu.greenField.repository.ProvinceLivraisonRepository;

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

    public ProvinceLivraison findByWordInNom(String word) {
        return provinceLivraisonRepository.findFirstByNomContainingIgnoreCase(word).orElse(null);
    }
}
