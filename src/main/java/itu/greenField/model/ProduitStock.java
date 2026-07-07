package itu.greenField.model;

public class ProduitStock {
    private Produit produit;
    private String pointDeVenteCode;
    private String pointDeVenteNom;
    private Integer stock;

    public ProduitStock(Produit produit, String pointDeVenteCode, String pointDeVenteNom, Integer stock) {
        this.produit = produit;
        this.pointDeVenteCode = pointDeVenteCode;
        this.pointDeVenteNom = pointDeVenteNom;
        this.stock = stock;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public String getPointDeVenteCode() {
        return pointDeVenteCode;
    }

    public void setPointDeVenteCode(String pointDeVenteCode) {
        this.pointDeVenteCode = pointDeVenteCode;
    }

    public String getPointDeVenteNom() {
        return pointDeVenteNom;
    }

    public void setPointDeVenteNom(String pointDeVenteNom) {
        this.pointDeVenteNom = pointDeVenteNom;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
