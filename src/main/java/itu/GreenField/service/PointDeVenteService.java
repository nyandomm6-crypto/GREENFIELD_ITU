package itu.GreenField.service;

import itu.GreenField.repository.PointDeVenteRepository;
import itu.GreenField.model.PointDeVente;

import org.springframework.stereotype.Service;

@Service
public class PointDeVenteService {
    private final PointDeVenteRepository repository;

    public PointDeVenteService(PointDeVenteRepository repository) {
        this.repository = repository;
    }

    public PointDeVente findPointDeVenteById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}
