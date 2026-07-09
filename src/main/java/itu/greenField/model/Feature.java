package itu.greenField.model;

public class Feature {
    private String icon;
    private String titre;
    private String description;

    public Feature(String icon, String titre, String description) {
        this.icon = icon;
        this.titre = titre;
        this.description = description;
    }

    // Getters et Setters
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}