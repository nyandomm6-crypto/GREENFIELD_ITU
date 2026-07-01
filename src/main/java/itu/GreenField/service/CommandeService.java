package itu.GreenField.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.GreenField.model.Client;
import itu.GreenField.model.Commandes;
import itu.GreenField.model.DetailsCommande;
import itu.GreenField.model.ModeReception;
import itu.GreenField.model.MvtStock;
import itu.GreenField.model.MvtStockFille;
import itu.GreenField.model.Panier;
import itu.GreenField.model.PanierFille;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.model.TypeMvt;
import itu.GreenField.repository.CommandesRepository;
import itu.GreenField.repository.DetailsCommandeRepository;
import itu.GreenField.repository.MvtStockFilleRepository;
import itu.GreenField.repository.MvtStockRepository;

@Service
public class CommandeService {

    private final CommandesRepository commandesRepository;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final ProduitService produitService;
    private final PanierService panierService;
    private final MvtStockRepository mvtStockRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;

    public CommandeService(CommandesRepository commandesRepository,
            DetailsCommandeRepository detailsCommandeRepository,
            ProduitService produitService,
            PanierService panierService,
            MvtStockRepository mvtStockRepository,
            MvtStockFilleRepository mvtStockFilleRepository) {
        this.commandesRepository = commandesRepository;
        this.detailsCommandeRepository = detailsCommandeRepository;
        this.produitService = produitService;
        this.panierService = panierService;
        this.mvtStockRepository = mvtStockRepository;
        this.mvtStockFilleRepository = mvtStockFilleRepository;
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
        commande.setDatecommande(LocalDateTime.now());
        commande.setModeReception(ModeReception.Retrait_Boutique);
        commande.setStatutCommande(StatutCommande.Cree);
        commande.setFraisLivraison(BigDecimal.ZERO);
        commande.setTotalProduits(total);
        commande.setTotalGeneral(total);

        commande = commandesRepository.save(commande);

        // En-tête du mouvement de stock correspondant à cette vente.
        MvtStock mvtStock = new MvtStock();
        mvtStock.setTypeMouvement(TypeMvt.Vente_Client);
        mvtStock = mvtStockRepository.save(mvtStock);

        for (PanierFille ligne : lignes) {
            DetailsCommande detail = new DetailsCommande();
            detail.setCommande(commande);
            detail.setProduit(ligne.getProduit());
            detail.setQuantite(ligne.getQuantite());
            detail.setPuAuMomentAchat(ligne.getProduit().getPu());
            detailsCommandeRepository.save(detail);

    
            MvtStockFille sortie = new MvtStockFille();
            sortie.setMvtStock(mvtStock);
            sortie.setProduit(ligne.getProduit());
            sortie.setQuantite(ligne.getQuantite());
            mvtStockFilleRepository.save(sortie);
        }

        panierService.vider(panier);
        return null;
    }
}
