package itu.greenField.model;

public class Banniere {
    private Long id;
    private String titre;
    private String sousTitre;
    private String description;
    private String imagePath;
    private String lien;
    private String btnTexte;
    private String promoNombre;
    private String promoPrix;
    private String promoUnite;

    // Constructeurs
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

    // Getters et Setters
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