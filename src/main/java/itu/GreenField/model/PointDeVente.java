package itu.GreenField.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pointdevente")
public class PointDeVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 255)
    private String adresse;

    @Column(nullable = false, length = 50)
    private String contact;

    @OneToMany(mappedBy = "pointDeVente")
    private List<DemandeStock> demandesStock;

    @OneToMany(mappedBy = "pointDeVente")
    private List<Employes> employes;

    @OneToMany(mappedBy = "pointDeVente")
    private List<MvtStock> mouvementsStock;

    @OneToMany(mappedBy = "pointDeVenteRetrait")
    private List<Commandes> commandesRetrait;

    @OneToMany(mappedBy = "pointDeVenteSource")
    private List<Transferts> transfertsDepart;

    @OneToMany(mappedBy = "pointDeVenteCible")
    private List<Transferts> transfertsArrivee;

    @OneToMany(mappedBy = "pointDeVente")
    private List<Notifications> notifications;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<DemandeStock> getDemandesStock() {
        return demandesStock;
    }

    public void setDemandesStock(List<DemandeStock> demandesStock) {
        this.demandesStock = demandesStock;
    }

    public List<Employes> getEmployes() {
        return employes;
    }

    public void setEmployes(List<Employes> employes) {
        this.employes = employes;
    }

    public List<MvtStock> getMouvementsStock() {
        return mouvementsStock;
    }

    public void setMouvementsStock(List<MvtStock> mouvementsStock) {
        this.mouvementsStock = mouvementsStock;
    }

    public List<Commandes> getCommandesRetrait() {
        return commandesRetrait;
    }

    public void setCommandesRetrait(List<Commandes> commandesRetrait) {
        this.commandesRetrait = commandesRetrait;
    }

    public List<Transferts> getTransfertsDepart() {
        return transfertsDepart;
    }

    public void setTransfertsDepart(List<Transferts> transfertsDepart) {
        this.transfertsDepart = transfertsDepart;
    }

    public List<Transferts> getTransfertsArrivee() {
        return transfertsArrivee;
    }

    public void setTransfertsArrivee(List<Transferts> transfertsArrivee) {
        this.transfertsArrivee = transfertsArrivee;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }
}