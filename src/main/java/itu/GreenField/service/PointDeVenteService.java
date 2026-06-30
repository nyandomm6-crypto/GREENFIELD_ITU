package itu.GreenField.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import itu.GreenField.model.PointDeVente;
import itu.GreenField.repository.PointDeVenteRepository;

@Service
public class PointDeVenteService {

    private PointDeVenteRepository pointDeVenteRepository;

    public PointDeVenteService(PointDeVenteRepository pointDeVenteRepository) {
        this.pointDeVenteRepository = pointDeVenteRepository;
    }

    public List<PointDeVente> getAllPointDeVente() {
        return pointDeVenteRepository.findAll();
    }

    public Optional<PointDeVente> getPointDeVenteById(Integer id) {
        return pointDeVenteRepository.findById(id);
    }

    public PointDeVente createPointDeVente(PointDeVente pointDeVente) {
        return pointDeVenteRepository.save(pointDeVente);
    }

    public PointDeVente updatePointDeVente(Integer id, PointDeVente pointDeVente) {
        Optional<PointDeVente> existingPointDeVente = pointDeVenteRepository.findById(id);
        if (existingPointDeVente.isPresent()) {
            PointDeVente updatedPointDeVente = existingPointDeVente.get();
            updatedPointDeVente.setNom(pointDeVente.getNom());
            updatedPointDeVente.setCode(pointDeVente.getCode());
            updatedPointDeVente.setAdresse(pointDeVente.getAdresse());
            updatedPointDeVente.setContact(pointDeVente.getContact());
            return pointDeVenteRepository.save(updatedPointDeVente);
        }
        return null;
    }

    public void deletePointDeVente(Integer id) {
        pointDeVenteRepository.deleteById(id);
    }
}
