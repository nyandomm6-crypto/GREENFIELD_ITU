package itu.GreenField.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TransfertDetailResponse {
    private Long id;
    private String codePointDeVenteSource;
    private String codePointDeVenteCible;
    private String statutTransfert;
    private LocalDateTime dateTransfert;
    private List<LigneTransfertDTO> lignes;

    public TransfertDetailResponse() {
    }

    public TransfertDetailResponse(Long id, String codePointDeVenteSource, String codePointDeVenteCible,
            String statutTransfert, LocalDateTime dateTransfert, List<LigneTransfertDTO> lignes) {
        this.id = id;
        this.codePointDeVenteSource = codePointDeVenteSource;
        this.codePointDeVenteCible = codePointDeVenteCible;
        this.statutTransfert = statutTransfert;
        this.dateTransfert = dateTransfert;
        this.lignes = lignes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodePointDeVenteSource() {
        return codePointDeVenteSource;
    }

    public void setCodePointDeVenteSource(String codePointDeVenteSource) {
        this.codePointDeVenteSource = codePointDeVenteSource;
    }

    public String getCodePointDeVenteCible() {
        return codePointDeVenteCible;
    }

    public void setCodePointDeVenteCible(String codePointDeVenteCible) {
        this.codePointDeVenteCible = codePointDeVenteCible;
    }

    public String getStatutTransfert() {
        return statutTransfert;
    }

    public void setStatutTransfert(String statutTransfert) {
        this.statutTransfert = statutTransfert;
    }

    public LocalDateTime getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDateTime dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public List<LigneTransfertDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneTransfertDTO> lignes) {
        this.lignes = lignes;
    }

    // Classe interne LigneTransfertDTO
    public static class LigneTransfertDTO {
        private Integer idProduit;
        private String nomProduit;
        private Integer quantiteDemandee;
        private Integer quantiteTransferee;
        private boolean transfertPartiel;

        public LigneTransfertDTO() {
        }

        public LigneTransfertDTO(Integer idProduit, String nomProduit, Integer quantiteDemandee,
                Integer quantiteTransferee, boolean transfertPartiel) {
            this.idProduit = idProduit;
            this.nomProduit = nomProduit;
            this.quantiteDemandee = quantiteDemandee;
            this.quantiteTransferee = quantiteTransferee;
            this.transfertPartiel = transfertPartiel;
        }

        public Integer getIdProduit() {
            return idProduit;
        }

        public void setIdProduit(Integer idProduit) {
            this.idProduit = idProduit;
        }

        public String getNomProduit() {
            return nomProduit;
        }

        public void setNomProduit(String nomProduit) {
            this.nomProduit = nomProduit;
        }

        public Integer getQuantiteDemandee() {
            return quantiteDemandee;
        }

        public void setQuantiteDemandee(Integer quantiteDemandee) {
            this.quantiteDemandee = quantiteDemandee;
        }

        public Integer getQuantiteTransferee() {
            return quantiteTransferee;
        }

        public void setQuantiteTransferee(Integer quantiteTransferee) {
            this.quantiteTransferee = quantiteTransferee;
        }

        public boolean isTransfertPartiel() {
            return transfertPartiel;
        }

        public void setTransfertPartiel(boolean transfertPartiel) {
            this.transfertPartiel = transfertPartiel;
        }
    }
}