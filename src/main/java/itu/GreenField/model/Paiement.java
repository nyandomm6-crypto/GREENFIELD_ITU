package itu.GreenField.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "paiement")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcommande")
    private Commandes commande;

    @Enumerated(EnumType.STRING)
    private StatutPaiement statut;

    @OneToMany(mappedBy = "paiement")
    private List<PaiementFille> filles;

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

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public List<PaiementFille> getFilles() {
        return filles;
    }

    public void setFilles(List<PaiementFille> filles) {
        this.filles = filles;
    }
}
