package itu.greenfield.model;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "livraisonfille")
public class LivraisonFille {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlivraison")
    private Livraison livraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcommande")
    private Commandes commande;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutlivraisonfille")
    private StatutLivraison statutLivraisonFille;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Commandes getCommande() {
        return commande;
    }

    public void setCommande(Commandes commande) {
        this.commande = commande;
    }

    public StatutLivraison getStatutLivraisonFille() {
        return statutLivraisonFille;
    }

    public void setStatutLivraisonFille(StatutLivraison statutLivraisonFille) {
        this.statutLivraisonFille = statutLivraisonFille;
    }
}
