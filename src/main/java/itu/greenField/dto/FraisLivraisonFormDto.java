package itu.greenField.dto;

import itu.greenField.model.FraisLivraison; // Assure-toi que le package de ton modèle est correct
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FraisLivraisonFormDto {
    private Integer id;

    @NotNull(message = "Veuillez sélectionner une province")
    private Integer idProvince;

    @NotNull(message = "Le poids de référence est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le poids de référence doit être supérieur ou égal à 0")
    private BigDecimal poidsreference;

    @NotNull(message = "Le montant du frais est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le montant ne peut pas être négatif")
    private BigDecimal montant;

    // Constructeur pour l'initialisation (Cas : Modification / Edition)
    public FraisLivraisonFormDto(FraisLivraison frais) {
        this.id = frais.getId();
        if (frais.getProvinceLivraison() != null) {
            this.idProvince = frais.getProvinceLivraison().getId();
        }
        this.poidsreference = frais.getPoidsReference();
        this.montant = frais.getMontant();
    }

    // Constructeur par défaut (Cas : Nouvelle Insertion)
    public FraisLivraisonFormDto() {
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(Integer idProvince) {
        this.idProvince = idProvince;
    }

    public BigDecimal getPoidsreference() {
        return poidsreference;
    }

    public void setPoidsreference(BigDecimal poidsreference) {
        this.poidsreference = poidsreference;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}