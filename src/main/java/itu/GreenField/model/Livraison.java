package itu.GreenField.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "livraison")
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvehicule")
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlivreur")
    private Employes livreur;

    @Column(name = "datelivraison")
    private LocalDateTime dateLivraison;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutlivraison")
    private StatutLivraison statutLivraison;

    @OneToMany(mappedBy = "livraison")
    private List<LivraisonFille> filles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Employes getLivreur() {
        return livreur;
    }

    public void setLivreur(Employes livreur) {
        this.livreur = livreur;
    }

    public LocalDateTime getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDateTime dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public StatutLivraison getStatutLivraison() {
        return statutLivraison;
    }

    public void setStatutLivraison(StatutLivraison statutLivraison) {
        this.statutLivraison = statutLivraison;
    }

    public List<LivraisonFille> getFilles() {
        return filles;
    }

    public void setFilles(List<LivraisonFille> filles) {
        this.filles = filles;
    }
}
