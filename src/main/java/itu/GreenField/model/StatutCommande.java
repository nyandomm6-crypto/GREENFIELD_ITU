package itu.greenfield.model;

import java.util.List;

public enum StatutCommande {
    Cree,
    En_cours,
    Paye,
    Annule;


    public static List<String> getAllStatutCommande() {
        return List.of(Cree.name(), En_cours.name(), Paye.name(), Annule.name());
    }
}
