package itu.GreenField.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TransfertsFille")
public class TransfertsFille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTransfert")
    private Transferts transfert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproduit")
    private Produit produit;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    public TransfertsFille() {
    }

    public TransfertsFille(Long id, Transferts transfert, Produit produit, Integer quantite) {
        this.id = id;
        this.transfert = transfert;
        this.produit = produit;
        this.quantite = quantite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transferts getTransfert() {
        return transfert;
    }

    public void setTransfert(Transferts transfert) {
        this.transfert = transfert;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}
