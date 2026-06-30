package itu.GreenField.service;

import org.springframework.stereotype.Service;

import itu.GreenField.model.StatutCommande;
import itu.GreenField.repository.StatutCommandeRepository;

import java.util.*;

@Service
public class StatutCommandeService {
    private final StatutCommandeRepository repository;

    public StatutCommandeService(StatutCommandeRepository repository) {
        this.repository = repository;
    }

    public List<StatutCommande> getAll(){
        return repository.findAll();
    }

    public StatutCommande findByNom(String nom){
        return repository.findFirstByNom(nom).orElse(null);
    }
    
}
