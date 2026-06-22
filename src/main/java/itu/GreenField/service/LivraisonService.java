package itu.GreenField.service;

import org.springframework.stereotype.Service;

import itu.GreenField.repository.LivraisonRepository;

@Service
public class LivraisonService {
    private LivraisonRepository livraisonRepository;

    public LivraisonService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }
}
