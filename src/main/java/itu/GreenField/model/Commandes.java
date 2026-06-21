package itu.greenfield.model;

import java.math.BigDecimal;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "commandes")
public class Commandes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idclient")
    private Client client;

    private LocalDateTime datecommande;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reception", nullable = false)
    private ModeReception modeReception;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idptdevente_retrait", referencedColumnName = "code")
    private PointDeVente pointDeVenteRetrait;

    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    @Column(name = "plage_horaire_souhaitee", length = 100)
    private String plageHoraireSouhaitee;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutcommande", nullable = false)
    private StatutCommande statutCommande;

    @Column(name = "frais_livraison", precision = 10, scale = 2)
    private BigDecimal fraisLivraison;

    @Column(name = "total_produits", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalProduits;

    @Column(name = "total_general", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGeneral;

    @OneToMany(mappedBy = "commande")
    private List<DetailsCommande> details;

    @OneToOne(mappedBy = "commande")
    private Paiement paiement;

    @OneToMany(mappedBy = "commande")
    private List<LivraisonFille> livraisonsFille;

    @OneToMany(mappedBy = "commande")
    private List<Tresorerie> tresoreries;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDatecommande() {
        return datecommande;
    }

    public void setDatecommande(LocalDateTime datecommande) {
        this.datecommande = datecommande;
    }

    public ModeReception getModeReception() {
        return modeReception;
    }

    public void setModeReception(ModeReception modeReception) {
        this.modeReception = modeReception;
    }

    public PointDeVente getPointDeVenteRetrait() {
        return pointDeVenteRetrait;
    }

    public void setPointDeVenteRetrait(PointDeVente pointDeVenteRetrait) {
        this.pointDeVenteRetrait = pointDeVenteRetrait;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getPlageHoraireSouhaitee() {
        return plageHoraireSouhaitee;
    }

    public void setPlageHoraireSouhaitee(String plageHoraireSouhaitee) {
        this.plageHoraireSouhaitee = plageHoraireSouhaitee;
    }

    public StatutCommande getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(StatutCommande statutCommande) {
        this.statutCommande = statutCommande;
    }

    public BigDecimal getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(BigDecimal fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public BigDecimal getTotalProduits() {
        return totalProduits;
    }

    public void setTotalProduits(BigDecimal totalProduits) {
        this.totalProduits = totalProduits;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public List<DetailsCommande> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsCommande> details) {
        this.details = details;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public List<LivraisonFille> getLivraisonsFille() {
        return livraisonsFille;
    }

    public void setLivraisonsFille(List<LivraisonFille> livraisonsFille) {
        this.livraisonsFille = livraisonsFille;
    }

    public List<Tresorerie> getTresoreries() {
        return tresoreries;
    }

    public void setTresoreries(List<Tresorerie> tresoreries) {
        this.tresoreries = tresoreries;
    }
}
