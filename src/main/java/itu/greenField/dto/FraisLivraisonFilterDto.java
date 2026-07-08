package itu.greenfield.dto;

import java.util.ArrayList;
import java.util.List;

public class FraisLivraisonFilterDto {
    private Integer lineNumber = 10;
    private Integer pageNumber = 1;

    // Filtre par Province (Idéalement une liste si l'utilisateur veut cocher plusieurs provinces)
    private List<Integer> idProvince = new ArrayList<>();

    // Structure dynamique pour les filtres numériques (Poids et Montant)
    // Permet de gérer : "poidsreference >= 50" ou "montant <= 15000"
    private List<String> typeFiltreNombre = new ArrayList<>(); // Ex: "poidsreference", "montant"
    private List<String> operateurNombre = new ArrayList<>();    // Ex: ">=", "<=", "=", "between"
    private List<Double> nombreValue = new ArrayList<>();      // Ex: 50.0, 15000.0

    // Constructeurs
    public FraisLivraisonFilterDto() {}

    // Getters et Setters
    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<Integer> getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(List<Integer> idProvince) {
        this.idProvince = idProvince;
    }

    public List<String> getTypeFiltreNombre() {
        return typeFiltreNombre;
    }

    public void setTypeFiltreNombre(List<String> typeFiltreNombre) {
        this.typeFiltreNombre = typeFiltreNombre;
    }

    public List<String> getOperateurNombre() {
        return operateurNombre;
    }

    public void setOperateurNombre(List<String> operateurNombre) {
        this.operateurNombre = operateurNombre;
    }

    public List<Double> getNombreValue() {
        return nombreValue;
    }

    public void setNombreValue(List<Double> nombreValue) {
        this.nombreValue = nombreValue;
    }
}
