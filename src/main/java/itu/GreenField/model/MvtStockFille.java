package itu.greenField.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mvtstockfille")
public class MvtStockFille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idmvtstock", nullable = false)
    private MvtStock mvtStock;

    @ManyToOne
    @JoinColumn(name = "idproduit", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    // getters & setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MvtStock getMvtStock() {
        return mvtStock;
    }

    public void setMvtStock(MvtStock mvtStock) {
        this.mvtStock = mvtStock;
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