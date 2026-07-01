package itu.GreenField.dto;

public class ProduitQuantiteDTO {
    private Integer idProduit;
    private Integer quantite;

    public ProduitQuantiteDTO() {
    }

    public ProduitQuantiteDTO(Integer idProduit, Integer quantite) {
        this.idProduit = idProduit;
        this.quantite = quantite;
    }

    public Integer getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}