package itu.greenField.filtre;

public enum FiltreNombreBackFraisOption {
    ALL("", "Tous"),
    POIDS_REFERENCE("poidsreference", "Poids de référence"),
    MONTANT("montant", "Montant du frais");

    private final String value;
    private final String label;

    FiltreNombreBackFraisOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }
}