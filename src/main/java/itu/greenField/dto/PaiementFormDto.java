package itu.greenField.dto;

import java.util.ArrayList;
import java.util.List;

public class PaiementFormDto {
    private Integer commandeId;
    private List<PaiementLigneDto> lignes = new ArrayList<>();

    public Integer getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Integer commandeId) {
        this.commandeId = commandeId;
    }

    public List<PaiementLigneDto> getLignes() {
        return lignes;
    }

    public void setLignes(List<PaiementLigneDto> lignes) {
        this.lignes = lignes;
    }
}
