package itu.greenField.model;

import java.util.List;

public enum TypeCommande {
    En_ligne,
    En_boutique;

    public static TypeCommande fromString(String value) {
        for (TypeCommande type : TypeCommande.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Valeur de type de commande invalide: " + value);
    }

    public static List<String> getAllTypeCommande() {
        return List.of(En_ligne.name(), En_boutique.name());
    }
}
