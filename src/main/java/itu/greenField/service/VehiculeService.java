package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.Vehicule;
import itu.greenField.repository.VehiculeRepository;

@Service
public class VehiculeService {
    private VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<Vehicule> getVehicule() {
        return vehiculeRepository.findAll();
    }
}
