package itu.GreenField.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Transferts") // ✅ Nom exact de la table dans PostgreSQL
public class Transferts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPointDeVente", referencedColumnName = "code") // ✅ Nom exact de la colonne
    private PointDeVente pointDeVenteSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPointDeVenteCible", referencedColumnName = "code") // ✅ Nom exact de la colonne
    private PointDeVente pointDeVenteCible;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_transfert", nullable = false)
    private StatutTransfert statutTransfert;

    @Column(name = "date_transfert")
    private LocalDateTime dateTransfert;

    @OneToMany(mappedBy = "transfert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransfertsFille> lignes = new ArrayList<>();

    public Transferts() {
    }

    public Transferts(Long id, PointDeVente pointDeVenteSource, PointDeVente pointDeVenteCible,
            StatutTransfert statutTransfert, LocalDateTime dateTransfert, List<TransfertsFille> lignes) {
        this.id = id;
        this.pointDeVenteSource = pointDeVenteSource;
        this.pointDeVenteCible = pointDeVenteCible;
        this.statutTransfert = statutTransfert;
        this.dateTransfert = dateTransfert;
        this.lignes = lignes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PointDeVente getPointDeVenteSource() {
        return pointDeVenteSource;
    }

    public void setPointDeVenteSource(PointDeVente pointDeVenteSource) {
        this.pointDeVenteSource = pointDeVenteSource;
    }

    public PointDeVente getPointDeVenteCible() {
        return pointDeVenteCible;
    }

    public void setPointDeVenteCible(PointDeVente pointDeVenteCible) {
        this.pointDeVenteCible = pointDeVenteCible;
    }

    public StatutTransfert getStatutTransfert() {
        return statutTransfert;
    }

    public void setStatutTransfert(StatutTransfert statutTransfert) {
        this.statutTransfert = statutTransfert;
    }

    public LocalDateTime getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDateTime dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public List<TransfertsFille> getLignes() {
        return lignes;
    }

    public void setLignes(List<TransfertsFille> lignes) {
        this.lignes = lignes;
    }
}