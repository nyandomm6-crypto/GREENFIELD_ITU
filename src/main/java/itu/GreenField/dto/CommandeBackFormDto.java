package itu.greenfield.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import itu.greenfield.model.Commandes;
import itu.greenfield.model.DetailsCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CommandeBackFormDto {
    private Integer commandeId;

    // Client
    private Integer clientId;
    @NotNull(message = "Le nom du client est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du client doit contenir entre 2 et 100 caractères")
    private String clientNom;

    @NotNull(message = "Le prénom du client est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom du client doit contenir entre 2 et 100 caractères")
    private String clientPrenom;

    // Commande
    @NotNull(message = "La date de la commande est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;

    @NotNull(message = "Le mode de réception est obligatoire")
    private String modeReception;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime heureReceptionDebut;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime heureReceptionFin;

    private String address;

    @NotEmpty(message = "La commande doit contenir au moins un produit.")
    @Valid
    private List<DetailCommandeBackDto> detailsCommande = new java.util.ArrayList<>();

    public CommandeBackFormDto(Commandes cmd) {
        commandeId = cmd.getId();
        clientId = cmd.getClient().getId();
        clientNom = cmd.getClient().getNom();
        clientPrenom = cmd.getClient().getPrenom();
        date = cmd.getDatecommande().toLocalDateTime();
        modeReception = cmd.getModeReception().name();
        if (cmd.getHeureReceptionDebut() != null) {
            heureReceptionDebut = cmd.getHeureReceptionDebut().toLocalDateTime();
        }
        if (cmd.getHeureReceptionFin() != null) {
            heureReceptionFin = cmd.getHeureReceptionFin().toLocalDateTime();
        }
        if (cmd.getAdresseLivraison() != null) {
            address = cmd.getAdresseLivraison();
        }
        for (DetailsCommande detail : cmd.getDetailsCommande()) {
            DetailCommandeBackDto dto = new DetailCommandeBackDto();
            dto.setProduitMatricule(detail.getProduit().getMatricule());
            dto.setQuantite(detail.getQuantite());
            detailsCommande.add(dto);
        }
    }

    public CommandeBackFormDto() {
    }

    public java.sql.Timestamp getSqlTypeOfDate() {
        if (date != null) {
            return java.sql.Timestamp.valueOf(date);
        }
        return null;
    }

    public java.sql.Timestamp getSqlTypeOfHeureReceptionDebut() {
        if (heureReceptionDebut != null) {
            return java.sql.Timestamp.valueOf(heureReceptionDebut);
        }
        return null;
    }

    public java.sql.Timestamp getSqlTypeOfHeureReceptionFin() {
        if (heureReceptionFin != null) {
            return java.sql.Timestamp.valueOf(heureReceptionFin);
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

    public LocalDateTime getHeureReceptionDebut() {
        return heureReceptionDebut;
    }

    public void setHeureReceptionDebut(LocalDateTime heureReceptionDebut) {
        this.heureReceptionDebut = heureReceptionDebut;
    }

    public LocalDateTime getHeureReceptionFin() {
        return heureReceptionFin;
    }

    public void setHeureReceptionFin(LocalDateTime heureReceptionFin) {
        this.heureReceptionFin = heureReceptionFin;
    }

    public List<DetailCommandeBackDto> getDetailsCommande() {
        return detailsCommande;
    }

    public void setDetailsCommande(List<DetailCommandeBackDto> detailsCommande) {
        this.detailsCommande = detailsCommande;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Integer commandeId) {
        this.commandeId = commandeId;
    }

}