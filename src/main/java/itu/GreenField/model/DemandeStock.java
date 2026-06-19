package itu.GreenField.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "demandestock")
public class DemandeStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idptdevente", referencedColumnName = "code")
    private PointDeVente pointDeVente;

    @Column(name = "datedemande")
    private LocalDateTime dateDemande;

    @Column(name = "datecible")
    private LocalDateTime dateCible;

    @OneToMany(mappedBy = "demandeStock")
    private List<DemandeStockFille> filles;

    @OneToMany(mappedBy = "demandeStock")
    private List<Notifications> notifications;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PointDeVente getPointDeVente() {
        return pointDeVente;
    }

    public void setPointDeVente(PointDeVente pointDeVente) {
        this.pointDeVente = pointDeVente;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDateTime getDateCible() {
        return dateCible;
    }

    public void setDateCible(LocalDateTime dateCible) {
        this.dateCible = dateCible;
    }

    public List<DemandeStockFille> getFilles() {
        return filles;
    }

    public void setFilles(List<DemandeStockFille> filles) {
        this.filles = filles;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }
}
