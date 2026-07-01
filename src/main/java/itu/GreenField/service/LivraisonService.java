package itu.GreenField.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Commandes;
import itu.GreenField.model.Employes;
import itu.GreenField.model.Livraison;
import itu.GreenField.model.LivraisonFille;
import itu.GreenField.model.StatutLivraison;
import itu.GreenField.model.Vehicule;
import itu.GreenField.repository.CommandesRepository;
import itu.GreenField.repository.EmployesRepository;
import itu.GreenField.repository.LivraisonFilleRepository;
import itu.GreenField.repository.LivraisonRepository;
import itu.GreenField.repository.VehiculeRepository;

@Service
public class LivraisonService {
    private LivraisonRepository livraisonRepository;
    private VehiculeRepository vehiculeRepository;
    private EmployesRepository employeRepository;
    private LivraisonFilleRepository livraisonFilleRepository;
    private CommandesRepository commandesRepository;

    public LivraisonService(LivraisonRepository livraisonRepository, VehiculeRepository vehiculeRepository,
            EmployesRepository employeRepository, LivraisonFilleRepository livraisonFilleRepository,
            CommandesRepository commandesRepository) {
        this.livraisonRepository = livraisonRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.employeRepository = employeRepository;
        this.livraisonFilleRepository = livraisonFilleRepository;
        this.commandesRepository = commandesRepository;
    }

    public Integer createLivraison(Integer idVehicule, Integer idEmploye, List<Integer> idCommande,
            LocalDateTime daty) {

        Vehicule vehicule = vehiculeRepository.getById(idVehicule);
        Employes employe = employeRepository.getById(idEmploye);

        Livraison livraison = new Livraison();
        livraison.setLivreur(employe);
        livraison.setVehicule(vehicule);
        livraison.setDateLivraison(daty);
        livraison.setStatutLivraison(StatutLivraison.En_attente);

        livraison = livraisonRepository.save(livraison);

        for (Integer id : idCommande) {
            Commandes commande = commandesRepository.getById(id);

            LivraisonFille livraisonFille = new LivraisonFille();
            livraisonFille.setCommande(commande);
            livraisonFille.setLivraison(livraison);
            livraisonFille.setStatutLivraisonFille(StatutLivraison.En_attente);

            livraisonFilleRepository.save(livraisonFille);
        }

        return livraison.getId();
    }

    public List<Livraison> getLivraisons() {
        return livraisonRepository.findAll();
    }

    public List<StatutLivraison> getStatutLivraisons() {
        StatutLivraison[] statuts = StatutLivraison.values();
        return Arrays.asList(statuts);
    }

    public List<Livraison> filtrer(
            StatutLivraison statut,
            Integer idVehicule,
            Integer idLivreur,
            LocalDate dateDebut,
            LocalDate dateFin) {

        return livraisonRepository.findAll()
                .stream()
                .filter(l -> statut == null || l.getStatutLivraison() == statut)
                .filter(l -> idVehicule == null ||
                        (l.getVehicule() != null
                                && l.getVehicule().getId().equals(idVehicule)))
                .filter(l -> idLivreur == null ||
                        (l.getLivreur() != null
                                && l.getLivreur().getId().equals(idLivreur)))
                .filter(l -> dateDebut == null ||
                        (l.getDateLivraison() != null &&
                                !l.getDateLivraison().toLocalDate().isBefore(dateDebut)))

                .filter(l -> dateFin == null ||
                        (l.getDateLivraison() != null &&
                                !l.getDateLivraison().toLocalDate().isAfter(dateFin)))
                .toList();
    }

    public Livraison getLivraisonById(Integer id) {
        return livraisonRepository.getById(id);
    }
}
