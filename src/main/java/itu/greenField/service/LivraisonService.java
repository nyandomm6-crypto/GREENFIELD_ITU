package itu.greenfield.service;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenfield.model.Commandes;
import itu.greenfield.model.Employes;
import itu.greenfield.model.Livraison;
import itu.greenfield.model.LivraisonFille;
import itu.greenfield.model.StatutLivraison;
import itu.greenfield.model.Vehicule;
import itu.greenfield.repository.CommandesRepository;
import itu.greenfield.repository.EmployesRepository;
import itu.greenfield.repository.LivraisonFilleRepository;
import itu.greenfield.repository.LivraisonRepository;
import itu.greenfield.repository.VehiculeRepository;

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

}
