package itu.GreenField.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DemandeTransfertRequest {
    private String codePointDeVenteDemandeur;
    private String objet;
    private String message;
    private LocalDateTime dateCible;
    private List<ProduitQuantiteDTO> produits;

    public DemandeTransfertRequest() {}

    public DemandeTransfertRequest(String codePointDeVenteDemandeur, String objet, String message, 
                                   LocalDateTime dateCible, List<ProduitQuantiteDTO> produits) {
        this.codePointDeVenteDemandeur = codePointDeVenteDemandeur;
        this.objet = objet;
        this.message = message;
        this.dateCible = dateCible;
        this.produits = produits;
    }

    public String getCodePointDeVenteDemandeur() { return codePointDeVenteDemandeur; }
    public void setCodePointDeVenteDemandeur(String codePointDeVenteDemandeur) { this.codePointDeVenteDemandeur = codePointDeVenteDemandeur; }
    
    public String getObjet() { return objet; }
    public void setObjet(String objet) { this.objet = objet; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getDateCible() { return dateCible; }
    public void setDateCible(LocalDateTime dateCible) { this.dateCible = dateCible; }
    
    public List<ProduitQuantiteDTO> getProduits() { return produits; }
    public void setProduits(List<ProduitQuantiteDTO> produits) { this.produits = produits; }
}