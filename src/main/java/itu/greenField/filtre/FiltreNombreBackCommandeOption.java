package itu.greenfield.filtre;

public enum FiltreNombreBackCommandeOption {
    ALL("", "Tous"),
    QUANTITE_TOTALE("total_produits", "Quantité totale"),
    VALEUR_TOTALE("total_general", "Montant total"),
    FRAIS_LIVRAISON("frais_livraison", "Frais livraison"),
    POIDS_TOTAL("poids_total", "Poids total");

    private final String value;
    private final String label;

    FiltreNombreBackCommandeOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }
}