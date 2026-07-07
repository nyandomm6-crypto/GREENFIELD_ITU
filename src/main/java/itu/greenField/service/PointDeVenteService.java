package itu.greenField.service;

import itu.greenField.repository.PointDeVenteRepository;
import itu.greenField.model.PointDeVente;

import java.util.List;

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

    public List<PointDeVente> getAll() {
        return repository.findAll();
    }
}
