package itu.greenField.dto;

import java.math.BigDecimal;

import itu.greenField.model.TypePayement;

public class PaiementLigneDto {
    private TypePayement typePayement;
    private BigDecimal montant;

    public PaiementLigneDto() {
    }

    public PaiementLigneDto(TypePayement typePayement, BigDecimal montant) {
        this.typePayement = typePayement;
        this.montant = montant;
    }

    public TypePayement getTypePayement() {
        return typePayement;
    }

    public void setTypePayement(TypePayement typePayement) {
        this.typePayement = typePayement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}
