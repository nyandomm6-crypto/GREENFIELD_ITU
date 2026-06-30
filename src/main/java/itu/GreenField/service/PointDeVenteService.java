package itu.greenfield.service;

import itu.greenfield.repository.PointDeVenteRepository;
import itu.greenfield.model.PointDeVente;

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
