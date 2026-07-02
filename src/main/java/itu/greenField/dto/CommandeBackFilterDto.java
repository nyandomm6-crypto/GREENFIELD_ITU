package itu.greenField.dto;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

public class CommandeBackFilterDto {
    Integer lineNumber = 10;
    Integer pageNumber = 1;
    List<Integer> clientId = new ArrayList<>();
    String modeReception;
    List<String> typeFiltreDate = new ArrayList<>();
    List<String> operateurDate = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    List<LocalDateTime> dateValue = new ArrayList<>();

    private List<String> typeFiltreNombre = new ArrayList<>();
    private List<String> operateurNombre = new ArrayList<>();
    private List<Double> nombreValue = new ArrayList<>();

    String statutCommande;
    String typeCommande;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<Integer> getClientId() {
        return clientId;
    }

    public void setClientId(List<Integer> clientId) {
        this.clientId = clientId;
    }

    public String getModeReception() {
        return modeReception;
    }

    public void setModeReception(String modeReception) {
        this.modeReception = modeReception;
    }

    public List<String> getTypeFiltreDate() {
        return typeFiltreDate;
    }

    public void setTypeFiltreDate(List<String> typeFiltreDate) {
        this.typeFiltreDate = typeFiltreDate;
    }

    public List<String> getOperateurDate() {
        return operateurDate;
    }

    public String getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(String typeCommande) {
        this.typeCommande = typeCommande;
    }

    public void setOperateurDate(List<String> operateurDate) {
        this.operateurDate = operateurDate;
    }

    public List<LocalDateTime> getDateValue() {
        return dateValue;
    }

    public void setDateValue(List<LocalDateTime> dateValue) {
        this.dateValue = dateValue;
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

    public String getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(String statutCommande) {
        this.statutCommande = statutCommande;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
