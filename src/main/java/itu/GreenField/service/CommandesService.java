package itu.GreenField.service;

import itu.GreenField.service.ProduitService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import itu.GreenField.dto.CommandeBackFormDto;
import itu.GreenField.dto.DetailCommandeBackDto;
import itu.GreenField.model.Client;
import itu.GreenField.model.Commandes;
import itu.GreenField.model.ModeReception;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.model.Produit;
import itu.GreenField.repository.CommandesRepository;
import itu.GreenField.repository.DetailsCommandeRepository;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.model.TypeCommande;
import itu.GreenField.model.DetailsCommande;

@Service
public class CommandesService {
    private final ProduitService produitService;
    private final CommandesRepository commandesRepository;
    private final ClientService clientService;
    private final PointDeVenteService pointDeVenteService;
    private final DetailsCommandeRepository detailsCommandeRepository;

    public CommandesService(ProduitService produitService, CommandesRepository commandesRepository,
            ClientService clientService, PointDeVenteService pointDeVenteService,
            DetailsCommandeRepository detailsCommandeRepository) {
        this.produitService = produitService;
        this.commandesRepository = commandesRepository;
        this.clientService = clientService;
        this.pointDeVenteService = pointDeVenteService;
        this.detailsCommandeRepository = detailsCommandeRepository;
    }

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findDispoCommandes();
    }

    public Commandes getCommandeById(Integer id) {
        return commandesRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveBackCommande(CommandeBackFormDto commandeFormDto) throws Exception {
        Integer clientId = commandeFormDto.getClientId();
        Client client = null;
        if (clientId != null) {
            client = clientService.getClientById(clientId);
        } else {
            client = new Client();
            client.setNom(commandeFormDto.getClientNom());
            client.setPrenom(commandeFormDto.getClientPrenom());
            client = clientService.saveClient(client);
        }

        ModeReception modeReception = ModeReception.fromString(commandeFormDto.getModeReception());
        StatutCommande statusCommande = StatutCommande.En_cours;
        TypeCommande typeCommande = TypeCommande.En_boutique;

        Commandes commande = new Commandes();
        commande.setClient(client);
        commande.setDatecommande(commandeFormDto.getSqlTypeOfDate());
        commande.setModeReception(modeReception);
        commande.setHeureReceptionDebut(commandeFormDto.getSqlTypeOfHeureReceptionDebut());
        commande.setHeureReceptionFin(commandeFormDto.getSqlTypeOfHeureReceptionFin());
        commande.setAdresseLivraison(commandeFormDto.getAddress());
        commande.setStatutCommande(statusCommande);
        commande.setTypeCommande(typeCommande);

        /* Static pour le moment */
        if(commandeFormDto.getAddress() == null || modeReception == ModeReception.Retrait_Boutique){
            PointDeVente pdv = pointDeVenteService.findPointDeVenteById(1);
            commande.setPointDeVenteRetrait(pdv);
        }

        /* Static pour le moment */
        commande.setFraisLivraison(BigDecimal.valueOf(5000.0));
        
        int qteTotal = 0;
        BigDecimal prixTotal = BigDecimal.ZERO;
        
        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        
        commande = commandesRepository.save(commande);
        int nbLines = commandeFormDto.getDetailsCommande().size();
        for (int i = 0; i < nbLines; i++) {
            DetailCommandeBackDto detailDto = commandeFormDto.getDetailsCommande().get(i);
            String matricule = detailDto.getProduitMatricule();
            Integer quantite = detailDto.getQuantite();
            DetailsCommande detail = new DetailsCommande();
            Produit produit = produitService.findProduitByMatricule(matricule);
            detail.setCommande(commande);
            detail.setProduit(produit);
            detail.setQuantite(quantite);
            detail.setPuAuMomentAchat(produit.getPu());
            detailsCommandeRepository.save(detail);

            qteTotal += quantite;
            prixTotal = prixTotal.add((BigDecimal.valueOf(quantite)).multiply(produit.getPu()));
        }

        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);

        commandesRepository.save(commande);
    }

}
