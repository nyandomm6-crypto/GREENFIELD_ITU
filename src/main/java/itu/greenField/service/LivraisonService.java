package itu.greenField.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.Livraison;
import itu.greenField.model.LivraisonFille;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.StatutLivraison;
import itu.greenField.model.Vehicule;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.EmployesRepository;
import itu.greenField.repository.LivraisonFilleRepository;
import itu.greenField.repository.LivraisonRepository;
import itu.greenField.repository.StatutCommandeRepository;
import itu.greenField.repository.VehiculeRepository;

@Service
public class LivraisonService {
    private LivraisonRepository livraisonRepository;
    private VehiculeRepository vehiculeRepository;
    private EmployesRepository employeRepository;
    private LivraisonFilleRepository livraisonFilleRepository;
    private CommandesRepository commandesRepository;
    private StatutCommandeRepository statutCommandeRepository;

    public LivraisonService(LivraisonRepository livraisonRepository, VehiculeRepository vehiculeRepository,
            EmployesRepository employeRepository, LivraisonFilleRepository livraisonFilleRepository,
            CommandesRepository commandesRepository, StatutCommandeRepository statutCommandeRepository) {
        this.livraisonRepository = livraisonRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.employeRepository = employeRepository;
        this.livraisonFilleRepository = livraisonFilleRepository;
        this.commandesRepository = commandesRepository;
        this.statutCommandeRepository = statutCommandeRepository;
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
            StatutCommande statut = statutCommandeRepository.findByNom("En cours de livraison")
                    .orElseThrow(() -> new RuntimeException("Statut introuvable"));
            commande.setStatutActuel(statut);
            commandesRepository.save(commande);
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
        return livraisonRepository.findById(id).orElse(null);
    }

    public List<Livraison> findByLivreur(Employes emp) {
        return livraisonRepository.findByLivreur(emp);
    }

    public void validerFille(Integer idLivraisonFille) {
        LivraisonFille lf = livraisonFilleRepository.findById(idLivraisonFille).orElse(null);
        lf.setStatutLivraisonFille(StatutLivraison.Livre);
        livraisonFilleRepository.save(lf);

    }

    public void valider(Integer idLivraison) {
        Livraison l = livraisonRepository.findById(idLivraison).orElse(null);
        l.setStatutLivraison(StatutLivraison.Livre);
        livraisonRepository.save(l);

    }

    public List<Livraison> findByLivreurDispo(Employes emp) {
        return livraisonRepository.findByLivreurDispo(emp.getId());
    }

    public boolean isMyCommande(Integer idCommande, Employes employe) {
        Commandes commande = commandesRepository.findById(idCommande).orElse(null);
        List<LivraisonFille> lf = livraisonFilleRepository.findByCommande(commande);
        for (LivraisonFille l : lf) {
            if (l.getLivraison().getLivreur().getId().equals(employe.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMyLivraisonFille(Integer idLivraisonFille, Employes employe) {
        LivraisonFille lf = livraisonFilleRepository.findById(idLivraisonFille).orElse(null);
        if (lf == null) {
            return false;
        }
        return lf.getLivraison().getLivreur().getId().equals(employe.getId());
    }

}
