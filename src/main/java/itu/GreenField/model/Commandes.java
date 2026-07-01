package itu.greenfield.model;

import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "idclient")
    private Client client;

    private java.sql.Timestamp datecommande;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reception", nullable = false)
    private ModeReception modeReception;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_commande")
    private TypeCommande typeCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idptdevente_retrait", referencedColumnName = "code")
    private PointDeVente pointDeVenteRetrait;

    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    @OneToOne
    @JoinColumn(name = "provincelivraisonid")
    private ProvinceLivraison provinceLivraison;

    @Column(name = "heure_reception_debut")
    private java.sql.Timestamp heureReceptionDebut;

    @Column(name = "heure_reception_fin")
    private java.sql.Timestamp heureReceptionFin;

    @Column(name = "frais_livraison", precision = 10, scale = 2)
    private BigDecimal fraisLivraison;

    @Column(name = "poids_total", precision = 10, scale = 2)
    private BigDecimal poidsTotal;

    @Column(name = "total_produits")
    private Integer totalProduits;

    @Column(name = "total_general", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGeneral;

    @OneToMany(mappedBy = "commande")
    private List<DetailsCommande> details;

    @OneToOne
    @JoinColumn(name = "statutactuel")
    private StatutCommande statutActuel;

    @OneToMany(mappedBy = "commande")
    private List<HistoriqueStatutCommande> historiqueStatut;

    @OneToOne(mappedBy = "commande")
    private Paiement paiement;

    @OneToMany(mappedBy = "commande")
    private List<LivraisonFille> livraisonsFille;

    @OneToMany(mappedBy = "commande")
    private List<Tresorerie> tresoreries;

    @OneToMany(mappedBy = "commande")
    private List<DetailsCommande> detailsCommande;

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

    public java.sql.Timestamp getDatecommande() {
        return datecommande;
    }

    public void setDatecommande(java.sql.Timestamp datecommande) {
        this.datecommande = datecommande;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }

    public ProvinceLivraison getProvinceLivraison() {
        return provinceLivraison;
    }

    public BigDecimal getPoidsTotal() {
        return poidsTotal;
    }

    public void setPoidsTotal(BigDecimal poidsTotal) {
        this.poidsTotal = poidsTotal;
    }

    public void setProvinceLivraison(ProvinceLivraison provinceLivraison) {
        this.provinceLivraison = provinceLivraison;
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

    public List<DetailsCommande> getDetailsCommande() {
        return detailsCommande;
    }

    public void setDetailsCommande(List<DetailsCommande> detailsCommande) {
        this.detailsCommande = detailsCommande;
    }

    public void setPointDeVenteRetrait(PointDeVente pointDeVenteRetrait) {
        this.pointDeVenteRetrait = pointDeVenteRetrait;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public StatutCommande getStatutActuel() {
        return statutActuel;
    }

    public void setStatutActuel(StatutCommande statutActuel) {
        this.statutActuel = statutActuel;
    }

    public List<HistoriqueStatutCommande> getHistoriqueStatut() {
        return historiqueStatut;
    }

    public void setHistoriqueStatut(List<HistoriqueStatutCommande> historiqueStatut) {
        this.historiqueStatut = historiqueStatut;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public java.sql.Timestamp getHeureReceptionDebut() {
        return heureReceptionDebut;
    }

    public void setHeureReceptionDebut(java.sql.Timestamp heureReceptionDebut) {
        this.heureReceptionDebut = heureReceptionDebut;
    }

    public java.sql.Timestamp getHeureReceptionFin() {
        return heureReceptionFin;
    }

    public void setHeureReceptionFin(java.sql.Timestamp heureReceptionFin) {
        this.heureReceptionFin = heureReceptionFin;
    }

    public BigDecimal getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(BigDecimal fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public Integer getTotalProduits() {
        return totalProduits;
    }

    public void setTotalProduits(Integer totalProduits) {
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
