package itu.greenfield.service;

import org.springframework.stereotype.Service;

import itu.greenfield.model.StatutCommande;
import itu.greenfield.repository.StatutCommandeRepository;

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
