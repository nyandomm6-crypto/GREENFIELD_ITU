package itu.GreenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Vehicule;
import itu.GreenField.repository.VehiculeRepository;

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
