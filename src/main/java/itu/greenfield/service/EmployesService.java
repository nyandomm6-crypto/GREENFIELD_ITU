package itu.GreenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Employes;
import itu.GreenField.repository.EmployesRepository;

@Service
public class EmployesService {
    private EmployesRepository employeRepository;

    public EmployesService(EmployesRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public List<Employes> getLivreur() {
        return employeRepository.findAll();
    }
}
