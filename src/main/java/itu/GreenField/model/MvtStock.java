package itu.greenfield.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mvtstock")
public class MvtStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false)
    private TypeMvt typeMouvement;

    @ManyToOne
    @JoinColumn(name = "idptdevente", referencedColumnName = "code", nullable = true)
    private PointDeVente pointDeVente;

    @Column(name = "datemvt")
    private LocalDateTime dateMvt;

    @PrePersist
    public void prePersist() {
        this.dateMvt = LocalDateTime.now();
    }

    // getters & setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeMvt getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMvt typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public PointDeVente getPointDeVente() {
        return pointDeVente;
    }

    public void setPointDeVente(PointDeVente pointDeVente) {
        this.pointDeVente = pointDeVente;
    }

    public LocalDateTime getDateMvt() {
        return dateMvt;
    }

    public void setDateMvt(LocalDateTime dateMvt) {
        this.dateMvt = dateMvt;
    }
}