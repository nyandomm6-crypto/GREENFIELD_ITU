package itu.greenField.filtre;

public enum FiltreDateBackCommandeOption {
    ALL("", "Tous"),
    DATE_COMMANDE("datecommande", "Date commande"),
    DATE_DEBUT("heure_reception_debut", "Début retrait"), // "Retrait" ou "Livraison" selon ton contexte
    DATE_FIN("heure_reception_fin", "Fin retrait");

    private final String value;
    private final String label;

    FiltreDateBackCommandeOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}