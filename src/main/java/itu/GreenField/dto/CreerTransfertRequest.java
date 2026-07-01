package itu.GreenField.dto;

import java.util.List;

public class CreerTransfertRequest {
    private String codePointDeVenteCible;
    private List<ProduitQuantiteDTO> produits;

    public CreerTransfertRequest() {
    }

    public CreerTransfertRequest(String codePointDeVenteCible, List<ProduitQuantiteDTO> produits) {
        this.codePointDeVenteCible = codePointDeVenteCible;
        this.produits = produits;
    }

    public String getCodePointDeVenteCible() {
        return codePointDeVenteCible;
    }

    public void setCodePointDeVenteCible(String codePointDeVenteCible) {
        this.codePointDeVenteCible = codePointDeVenteCible;
    }

    public List<ProduitQuantiteDTO> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitQuantiteDTO> produits) {
        this.produits = produits;
    }
}