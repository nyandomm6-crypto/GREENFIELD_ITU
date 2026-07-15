package itu.greenField.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "paiementfille")
public class PaiementFille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpaiement")
    private Paiement paiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "typepayement", nullable = false)
    private TypePayement typePayement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    @Column(name = "date")
    private LocalDate date;

    /**
     * Une ligne en Espèce ou Mobile Money n'est encaissée (entrée trésorerie)
     * qu'après confirmation manuelle par un caissier ou un administrateur.
     */
    @Column(name = "confirme", nullable = false)
    private boolean confirme;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public TypePayement getTypePayement() {
        return typePayement;
    }

    public void setTypePayement(TypePayement typePayement) {
        this.typePayement = typePayement;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isConfirme() {
        return confirme;
    }

    public void setConfirme(boolean confirme) {
        this.confirme = confirme;
    }

    /** Vrai si ce type de paiement exige une confirmation manuelle avant encaissement. */
    public boolean isAConfirmer() {
        return TypePayement.Espece.equals(typePayement) || TypePayement.Mobile_Money.equals(typePayement);
    }

    /** Vrai si la ligne attend encore une confirmation manuelle. */
    public boolean isEnAttente() {
        return isAConfirmer() && !confirme;
    }
}