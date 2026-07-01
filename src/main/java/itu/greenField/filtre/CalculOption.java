package itu.greenField.filtre;

public enum CalculOption {
    EGALE("=", "Egale"),
    INFERIEURE("<", "Inférieure"),
    SUPERIEURE(">", "Supérieure"),
    INF_EGALE("<=", "Inférieure ou égale"),
    SUP_EGALE(">=", "Supérieure ou égale");

    private final String value;
    private final String label;

    CalculOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }
}