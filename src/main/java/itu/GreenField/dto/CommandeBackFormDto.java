package itu.GreenField.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class CommandeBackFormDto {
    // Client
    private Integer clientId;
    private String clientNom;
    private String clientPrenom;

    // Commande
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;

    private String modeReception;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureReceptionDebut;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureReceptionFin;

    private String address;

    // Details
    private List<String> produitMatricule;
    private List<Integer> qte;

    public java.sql.Timestamp getSqlTypeOfDate() {
        if (date != null) {
            return java.sql.Timestamp.valueOf(date);
        }
        return null;
    }

    public java.sql.Time getSqlTypeOfHeureReceptionDebut() {
        if (heureReceptionDebut != null) {
            return java.sql.Time.valueOf(heureReceptionDebut);
        }
        return null;
    }

    public java.sql.Time getSqlTypeOfHeureReceptionFin() {
        if (heureReceptionFin != null) {
            return java.sql.Time.valueOf(heureReceptionFin);
        }
        return null;
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public String getClientPrenom() {
        return clientPrenom;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public void setClientPrenom(String clientPrenom) {
        this.clientPrenom = clientPrenom;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getModeReception() {
        return modeReception;
    }

    public void setModeReception(String modeReception) {
        this.modeReception = modeReception;
    }

    public LocalTime getHeureReceptionDebut() {
        return heureReceptionDebut;
    }

    public void setHeureReceptionDebut(LocalTime heureReceptionDebut) {
        this.heureReceptionDebut = heureReceptionDebut;
    }

    public LocalTime getHeureReceptionFin() {
        return heureReceptionFin;
    }

    public void setHeureReceptionFin(LocalTime heureReceptionFin) {
        this.heureReceptionFin = heureReceptionFin;
    }

    public List<String> getProduitMatricule() {
        return produitMatricule;
    }

    public void setProduitMatricule(List<String> produitMatricule) {
        this.produitMatricule = produitMatricule;
    }

    public List<Integer> getQte() {
        return qte;
    }

    public void setQte(List<Integer> qte) {
        this.qte = qte;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}