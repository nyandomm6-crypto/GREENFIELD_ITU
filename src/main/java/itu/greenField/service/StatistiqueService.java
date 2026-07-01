package itu.greenField.service;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;

import java.util.List;

public interface StatistiqueService {
    List<ProduitStatDto> getTop5Produits();
    List<Produit> getNouveauxProduits();
    Double getBeneficeFromage();
    List<EvolutionVenteDto> getEvolutionVentes();
    List<ClientStatDto> getTop5Clients();
}