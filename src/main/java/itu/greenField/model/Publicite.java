package itu.greenField.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "publicite")
public class Publicite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(name = "sous_titre", length = 150)
    private String sousTitre;

    @Column(length = 255)
    private String lien;

    @Column(name = "class_div", length = 255)
    private String classDiv;

    @Column(name = "class_content", length = 255)
    private String classContent;

    @Column(name = "class_titre", length = 255)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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