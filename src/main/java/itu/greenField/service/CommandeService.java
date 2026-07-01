package itu.greenField.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.ModeReception;
import itu.greenField.model.Panier;
import itu.greenField.model.PanierFille;
import itu.greenField.model.StatutCommande;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.DetailsCommandeRepository;

@Service
public class CommandeService {

    private final CommandesRepository commandesRepository;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final ProduitService produitService;
    private final PanierService panierService;

    public CommandeService(CommandesRepository commandesRepository,
            DetailsCommandeRepository detailsCommandeRepository,
            ProduitService produitService,
            PanierService panierService) {
        this.commandesRepository = commandesRepository;
        this.detailsCommandeRepository = detailsCommandeRepository;
        this.produitService = produitService;
        this.panierService = panierService;
    }

    /**
     * Valide l'achat : vérifie une dernière fois le stock de chaque ligne,
     * puis crée la commande et ses détails. Le prix unitaire est figé au
     * moment de l'achat (pu_au_moment_achat), conformément aux règles
     * d'historisation du projet. Le panier est vidé une fois la commande
     * créée.
     *
     * @return null si la commande a été créée, sinon un message d'erreur.
     */
    @Transactional
    public String validerAchat(Panier panier, Client client) {
        List<PanierFille> lignes = panierService.listerLignes(panier);

        if (lignes.isEmpty()) {
            return "Le panier est vide.";
        }

        for (PanierFille ligne : lignes) {
            int stockDisponible = produitService.calculerStock(ligne.getProduit().getId());
            if (ligne.getQuantite() > stockDisponible) {
                return "Stock insuffisant pour \"" + ligne.getProduit().getNom()
                        + "\" (disponible : " + stockDisponible + ").";
            }
        }

        BigDecimal total = panierService.calculerTotal(panier);

        Commandes commande = new Commandes();
        commande.setClient(client);
        // commande.setDatecommande(LocalDateTime.now());
        commande.setModeReception(ModeReception.Retrait_Boutique);
        // commande.setStatutCommande(StatutCommande.Cree);
        commande.setFraisLivraison(BigDecimal.ZERO);
        // commande.setTotalProduits(total);
        commande.setTotalGeneral(total);

        commande = commandesRepository.save(commande);

        for (PanierFille ligne : lignes) {
            DetailsCommande detail = new DetailsCommande();
            detail.setCommande(commande);
            detail.setProduit(ligne.getProduit());
            detail.setQuantite(ligne.getQuantite());
            detail.setPuAuMomentAchat(ligne.getProduit().getPu());
            detailsCommandeRepository.save(detail);
        }

        panierService.vider(panier);
        return null;
    }
}
