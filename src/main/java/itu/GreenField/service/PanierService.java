package itu.GreenField.service;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Produit;
import itu.GreenField.panier.Panier;

@Service
public class PanierService {

    private final ProduitService produitService;

    public PanierService(ProduitService produitService) {
        this.produitService = produitService;
    }

    /**
     * Ajoute un produit au panier en s'assurant que la quantite demandee ne
     * depasse pas le stock disponible.
     *
     * @return un message d'erreur si l'ajout est impossible, ou null si tout
     *         s'est bien passe.
     */
    public String ajouterAuPanier(Panier panier, Integer idProduit, int quantite) {
        if (quantite <= 0) {
            return "La quantité doit être supérieure à zéro.";
        }

        Produit produit = produitService.trouverParId(idProduit);
        if (produit == null) {
            return "Produit introuvable.";
        }

        int stockDisponible = produitService.calculerStock(idProduit);
        int dejaDansLePanier = panier.getLignes().containsKey(idProduit)
                ? panier.getLignes().get(idProduit).getQuantite()
                : 0;

        if (dejaDansLePanier + quantite > stockDisponible) {
            return "Stock insuffisant pour \"" + produit.getNom() + "\" (disponible : " + stockDisponible + ").";
        }

        panier.ajouterProduit(produit, quantite);
        return null;
    }

    /**
     * Modifie la quantite d'une ligne du panier, en respectant le stock
     * disponible.
     */
    public String modifierQuantite(Panier panier, Integer idProduit, int quantite) {
        if (quantite <= 0) {
            panier.supprimerProduit(idProduit);
            return null;
        }

        int stockDisponible = produitService.calculerStock(idProduit);
        if (quantite > stockDisponible) {
            return "Stock insuffisant (disponible : " + stockDisponible + ").";
        }

        panier.modifierQuantite(idProduit, quantite);
        return null;
    }
}
