package itu.GreenField.panier;

import java.io.Serializable;
import java.math.BigDecimal;

import itu.GreenField.model.Produit;


public class LignePanier implements Serializable {

    private Integer idProduit;
    private String nomProduit;
    private BigDecimal prixUnitaire;
    private Integer quantite;

    public LignePanier() {
    }

    public LignePanier(Produit produit, Integer quantite) {
        this.idProduit = produit.getId();
        this.nomProduit = produit.getNom();
        this.prixUnitaire = produit.getPu();
        this.quantite = quantite;
    }

    public Integer getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getSousTotal() {
        if (prixUnitaire == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}
