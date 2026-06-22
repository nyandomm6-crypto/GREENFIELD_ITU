package itu.greenfield.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "produit")
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false, unique = true, length = 50)
    private String matricule;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    @JsonIgnoreProperties("produits")  // évite la boucle Produit -> CategorieProduit -> [produits] -> Produit
    private CategorieProduit categorie;

    @JsonIgnore
    @OneToMany(mappedBy = "produit")
    private List<DemandeStockFille> demandesStockFille;

    @JsonIgnore
    @OneToMany(mappedBy = "produit")
    private List<DetailsCommande> detailsCommande;

    @JsonIgnore
    @OneToMany(mappedBy = "produit")
    private List<TransfertsFille> transfertsFille;

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

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public BigDecimal getPu() {
        return pu;
    }

    public void setPu(BigDecimal pu) {
        this.pu = pu;
    }

    public CategorieProduit getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
    }

    public List<DemandeStockFille> getDemandesStockFille() {
        return demandesStockFille;
    }

    public void setDemandesStockFille(List<DemandeStockFille> demandesStockFille) {
        this.demandesStockFille = demandesStockFille;
    }

    public List<DetailsCommande> getDetailsCommande() {
        return detailsCommande;
    }

    public void setDetailsCommande(List<DetailsCommande> detailsCommande) {
        this.detailsCommande = detailsCommande;
    }

    public List<TransfertsFille> getTransfertsFille() {
        return transfertsFille;
    }

    public void setTransfertsFille(List<TransfertsFille> transfertsFille) {
        this.transfertsFille = transfertsFille;
    }
}
