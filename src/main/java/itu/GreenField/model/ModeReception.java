package itu.GreenField.model;

public enum ModeReception {
    Retrait_Boutique,
    Livraison_Domicile;

    public static ModeReception fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mode de réception ne peut pas être vide ou nul.");
        }
        
        String cleanedValue = value.trim();

        for (ModeReception mode : ModeReception.values()) {
            if (mode.name().equalsIgnoreCase(cleanedValue)) {
                return mode;
            }
        }

        throw new IllegalArgumentException("Aucun ModeReception correspondant pour la valeur : " + value);
    }
}
