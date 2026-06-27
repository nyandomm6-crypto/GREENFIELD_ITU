package itu.GreenField.panier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import itu.GreenField.model.Produit;


public class Panier implements Serializable {

    private final Map<Integer, LignePanier> lignes = new LinkedHashMap<>();

    public void ajouterProduit(Produit produit, int quantite) {
        if (produit == null || quantite <= 0) {
            return;
        }

        LignePanier ligne = lignes.get(produit.getId());
        if (ligne == null) {
            lignes.put(produit.getId(), new LignePanier(produit, quantite));
        } else {
            ligne.setQuantite(ligne.getQuantite() + quantite);
        }
    }

    public void modifierQuantite(Integer idProduit, int quantite) {
        LignePanier ligne = lignes.get(idProduit);
        if (ligne == null) {
            return;
        }
        if (quantite <= 0) {
            lignes.remove(idProduit);
        } else {
            ligne.setQuantite(quantite);
        }
    }

    public void supprimerProduit(Integer idProduit) {
        lignes.remove(idProduit);
    }

    public void vider() {
        lignes.clear();
    }

    public Map<Integer, LignePanier> getLignes() {
        return lignes;
    }

    public int getNombreArticles() {
        return lignes.values().stream()
                .mapToInt(LignePanier::getQuantite)
                .sum();
    }

    public BigDecimal getTotal() {
        return lignes.values().stream()
                .map(LignePanier::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estVide() {
        return lignes.isEmpty();
    }
}
