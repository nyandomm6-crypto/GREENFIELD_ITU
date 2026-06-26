package itu.GreenField.dto;

import jakarta.validation.constraints.*;

public class DetailCommandeBackDto {
    @NotNull(message = "Le produit est obligatoire")
    @Size(min = 1, message = "Le produit est obligatoire")
    private String produitMatricule;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être un nombre positif")
    private Integer quantite;

    public String getProduitMatricule() {
        return produitMatricule;
    }

    public void setProduitMatricule(String produitMatricule) {
        this.produitMatricule = produitMatricule;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

}
