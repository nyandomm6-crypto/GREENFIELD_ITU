package itu.greenField.service;

import itu.greenField.model.Vehicule;
import itu.greenField.model.StatutVehicule;
import itu.greenField.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculeService {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    // II-Liste véhicule (avec recherche)
    public List<Vehicule> listerEtFiltrer(String motCle, LocalDate date, StatutVehicule statut) {
        if (motCle != null || date != null || statut != null) {
            return vehiculeRepository.filtrerVehicules(motCle, date, statut);
        }
        return vehiculeRepository.findAll();
    }

    // I.A - Voir détails (Get by ID)
    public Optional<Vehicule> obtenirParId(Integer id) {
        return vehiculeRepository.findById(id);
    }

    // I.D - Ajouter véhicule (Create)
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        if (vehicule.getDate() == null) {
            vehicule.setDate(LocalDate.now()); // Date du jour par défaut
        }
        return vehiculeRepository.save(vehicule);
    }

    // I.C - Modifier véhicule (Update)
    public Vehicule modifierVehicule(Integer id, Vehicule nouveauxDetails) {
        return vehiculeRepository.findById(id).map(vehicule -> {
            vehicule.setMatricule(nouveauxDetails.getMatricule());
            vehicule.setMarque(nouveauxDetails.getMarque());
            vehicule.setModele(nouveauxDetails.getModele());
            vehicule.setAnnee(nouveauxDetails.getAnnee());
            vehicule.setCapacite(nouveauxDetails.getCapacite());
            vehicule.setStatut(nouveauxDetails.getStatut());
            if (nouveauxDetails.getDate() != null) {
                vehicule.setDate(nouveauxDetails.getDate());
            }
            return vehiculeRepository.save(vehicule);
        }).orElseThrow(() -> new RuntimeException("Véhicule introuvable avec l'id : " + id));
    }

    // I.B - Supprimer
    public void supprimerVehicule(Integer id) {
        vehiculeRepository.deleteById(id);
    }
}