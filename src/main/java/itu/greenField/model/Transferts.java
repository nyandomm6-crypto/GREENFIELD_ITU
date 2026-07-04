package itu.greenField.model;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "transferts")
public class Transferts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpointdevente", referencedColumnName = "code")
    private PointDeVente pointDeVente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpointdeventecible", referencedColumnName = "code")
    private PointDeVente pointDeVenteCible;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_transfert", nullable = false)
    private StatutTransfert statutTransfert;

    @Column(name = "date_transfert")
    private LocalDateTime dateTransfert;

    @OneToMany(mappedBy = "transfert")
    private List<TransfertsFille> filles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PointDeVente getPointDeVente() {
        return pointDeVente;
    }

    public void setPointDeVente(PointDeVente pointDeVente) {
        this.pointDeVente = pointDeVente;
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

    public List<TransfertsFille> getFilles() {
        return filles;
    }

    public void setFilles(List<TransfertsFille> filles) {
        this.filles = filles;
    }
}
