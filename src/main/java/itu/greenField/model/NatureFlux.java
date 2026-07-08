package itu.greenField.model;

/**
 * Natures de mouvement de trésorerie proposées dans le formulaire
 * (dropdown « type de flux »). Chaque nature porte son sens : une entrée
 * d'argent (Entree_Vente) ou une dépense d'exploitation (Depense_Exploitation).
 */
public enum NatureFlux {
    Vente("Vente", TypeFlux.Entree_Vente),
    Autre_Entree("Autre entrée", TypeFlux.Entree_Vente),
    Carburant("Carburant", TypeFlux.Depense_Exploitation),
    Salaire("Salaire", TypeFlux.Depense_Exploitation),
    Loyer("Loyer", TypeFlux.Depense_Exploitation),
    Electricite("Électricité / Eau", TypeFlux.Depense_Exploitation),
    Fournitures("Fournitures", TypeFlux.Depense_Exploitation),
    Entretien_Vehicule("Entretien véhicule", TypeFlux.Depense_Exploitation),
    Transport("Transport / Livraison", TypeFlux.Depense_Exploitation),
    Autre_Depense("Autre dépense", TypeFlux.Depense_Exploitation);

    private final String libelle;
    private final TypeFlux sens;

    NatureFlux(String libelle, TypeFlux sens) {
        this.libelle = libelle;
        this.sens = sens;
    }

    public String getLibelle() {
        return libelle;
    }

    public TypeFlux getSens() {
        return sens;
    }

    public boolean estDepense() {
        return sens == TypeFlux.Depense_Exploitation;
    }
}
