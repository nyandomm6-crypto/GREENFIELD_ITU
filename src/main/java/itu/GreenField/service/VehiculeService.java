package itu.greenfield.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenfield.model.Vehicule;
import itu.greenfield.repository.VehiculeRepository;

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
