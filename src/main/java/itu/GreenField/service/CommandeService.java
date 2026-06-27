package itu.GreenField.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.GreenField.model.Client;
import itu.GreenField.model.Commandes;
import itu.GreenField.model.DetailsCommande;
import itu.GreenField.model.ModeReception;
import itu.GreenField.model.Produit;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.panier.LignePanier;
import itu.GreenField.panier.Panier;
import itu.GreenField.repository.CommandesRepository;
import itu.GreenField.repository.DetailsCommandeRepository;

@Service
public class CommandeService {

    private final CommandesRepository commandesRepository;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final ProduitService produitService;

    public CommandeService(CommandesRepository commandesRepository,
            DetailsCommandeRepository detailsCommandeRepository,
            ProduitService produitService) {
        this.commandesRepository = commandesRepository;
        this.detailsCommandeRepository = detailsCommandeRepository;
        this.produitService = produitService;
    }

    /**
     * Valide l'achat : vérifie une dernière fois le stock de chaque ligne,
     * puis crée la commande et ses détails. Le prix unitaire est figé au
     * moment de l'achat (pu_au_moment_achat), conformément aux règles
     * d'historisation du projet.
     *
     * @return null si la commande a été créée, sinon un message d'erreur.
     */
    @Transactional
    public String validerAchat(Panier panier, Client client) {
        if (panier == null || panier.estVide()) {
            return "Le panier est vide.";
        }

        for (LignePanier ligne : panier.getLignes().values()) {
            int stockDisponible = produitService.calculerStock(ligne.getIdProduit());
            if (ligne.getQuantite() > stockDisponible) {
                return "Stock insuffisant pour \"" + ligne.getNomProduit()
                        + "\" (disponible : " + stockDisponible + ").";
            }
        }

        Commandes commande = new Commandes();
        commande.setClient(client);
        commande.setDatecommande(LocalDateTime.now());
        commande.setModeReception(ModeReception.Retrait_Boutique);
        commande.setStatutCommande(StatutCommande.Cree);
        commande.setFraisLivraison(BigDecimal.ZERO);
        commande.setTotalProduits(panier.getTotal());
        commande.setTotalGeneral(panier.getTotal());

        commande = commandesRepository.save(commande);

        for (LignePanier ligne : panier.getLignes().values()) {
            Produit produit = produitService.trouverParId(ligne.getIdProduit());

            DetailsCommande detail = new DetailsCommande();
            detail.setCommande(commande);
            detail.setProduit(produit);
            detail.setQuantite(ligne.getQuantite());
            detail.setPuAuMomentAchat(ligne.getPrixUnitaire());
            detailsCommandeRepository.save(detail);
        }

        panier.vider();
        return null;
    }
}
