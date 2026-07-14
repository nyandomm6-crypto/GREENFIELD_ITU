package itu.greenField.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fraislivraison")
public class FraisLivraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idprovince")
    private ProvinceLivraison provinceLivraison;

    @Column(name = "poidsreference", nullable = false, precision = 10, scale = 2)
    private BigDecimal poidsReference;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal montant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProvinceLivraison getProvinceLivraison() {
        return provinceLivraison;
    }

    public void setProvinceLivraison(ProvinceLivraison provinceLivraison) {
        this.provinceLivraison = provinceLivraison;
    }

    public BigDecimal getPoidsReference() {
        return poidsReference;
    }

    public void setPoidsReference(BigDecimal poidsReference) {
        this.poidsReference = poidsReference;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

}
