package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.Employes;
import itu.greenField.repository.EmployesRepository;

@Service
public class EmployesService {
    private EmployesRepository employeRepository;

    public EmployesService(EmployesRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public List<Employes> getLivreur() {
        return employeRepository.findAll();
    }

    public Employes findByEmail(String email) {
        return employeRepository.findByMail(email).orElse(null);
    }
}
