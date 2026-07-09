package itu.greenField.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "banniere")
public class Banniere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(name = "sous_titre", length = 150)
    private String sousTitre;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(length = 255)
    private String lien;

    @Column(name = "btn_texte", length = 80)
    private String btnTexte;

    @Column(name = "promo_nombre", length = 50)
    private String promoNombre;

    @Column(name = "promo_prix", length = 50)
    private String promoPrix;

    @Column(name = "promo_unite", length = 50)
    private String promoUnite;

    public Banniere() {
    }

    public Banniere(Long id, String titre, String sousTitre, String description,
            String imagePath, String lien, String btnTexte,
            String promoNombre, String promoPrix, String promoUnite) {
        this.id = id;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.description = description;
        this.imagePath = imagePath;
        this.lien = lien;
        this.btnTexte = btnTexte;
        this.promoNombre = promoNombre;
        this.promoPrix = promoPrix;
        this.promoUnite = promoUnite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getBtnTexte() {
        return btnTexte;
    }

    public void setBtnTexte(String btnTexte) {
        this.btnTexte = btnTexte;
    }

    public String getPromoNombre() {
        return promoNombre;
    }

    public void setPromoNombre(String promoNombre) {
        this.promoNombre = promoNombre;
    }

    public String getPromoPrix() {
        return promoPrix;
    }

    public void setPromoPrix(String promoPrix) {
        this.promoPrix = promoPrix;
    }

    public String getPromoUnite() {
        return promoUnite;
    }

    public void setPromoUnite(String promoUnite) {
        this.promoUnite = promoUnite;
    }
}