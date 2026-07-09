package itu.greenField.model;

public class Publicite {
    private String imagePath;
    private String titre;
    private String sousTitre;
    private String lien;
    private String classDiv;
    private String classContent;
    private String classTitre;

    public Publicite() {
    }

    public Publicite(String imagePath, String titre, String sousTitre, String lien,
            String classDiv, String classContent, String classTitre) {
        this.imagePath = imagePath;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.lien = lien;
        this.classDiv = classDiv;
        this.classContent = classContent;
        this.classTitre = classTitre;
    }

    // Getters et Setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getClassDiv() {
        return classDiv;
    }

    public void setClassDiv(String classDiv) {
        this.classDiv = classDiv;
    }

    public String getClassContent() {
        return classContent;
    }

    public void setClassContent(String classContent) {
        this.classContent = classContent;
    }

    public String getClassTitre() {
        return classTitre;
    }

    public void setClassTitre(String classTitre) {
        this.classTitre = classTitre;
    }
}