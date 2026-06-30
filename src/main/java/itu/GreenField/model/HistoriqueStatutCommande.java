package itu.GreenField.model;

import jakarta.persistence.*;

@Entity
@Table(name = "histstatutcommande")
public class HistoriqueStatutCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idcommande")
    private Commandes commande;

    @ManyToOne
    @JoinColumn(name = "idstatut")
    private StatutCommande statutCommande;

    private java.sql.Timestamp datechangement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Commandes getCommande() {
        return commande;
    }

    public void setCommande(Commandes commande) {
        this.commande = commande;
    }

    public StatutCommande getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(StatutCommande statutCommande) {
        this.statutCommande = statutCommande;
    }

    public java.sql.Timestamp getDatechangement() {
        return datechangement;
    }

    public void setDatechangement(java.sql.Timestamp datechangement) {
        this.datechangement = datechangement;
    }

}
