package itu.greenField.service;

import org.springframework.stereotype.Service;

import itu.greenField.model.StatutCommande;
import itu.greenField.repository.StatutCommandeRepository;

import java.util.*;

@Service
public class StatutCommandeService {
    private final StatutCommandeRepository repository;

    public StatutCommandeService(StatutCommandeRepository repository) {
        this.repository = repository;
    }

    public List<StatutCommande> getAll() {
        return repository.findAll();
    }

    public StatutCommande findByNom(String nom) {
        return repository.findFirstByNom(nom).orElse(null);
    }

}
