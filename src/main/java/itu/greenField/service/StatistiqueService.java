package itu.greenField.service;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;

import java.util.List;
import java.util.Map;

public interface StatistiqueService {
    List<ProduitStatDto> getTop5Produits(Integer year);
    List<Produit> getNouveauxProduits();
    Map<String, Object> getTresorerieStats(Integer year, String dateDebut, String dateFin);
    List<EvolutionVenteDto> getEvolutionVentes(Integer idproduit, Integer year);
    List<ClientStatDto> getTop5Clients(Integer year);
    List<ProduitStatDto> getHistoriqueVentesGlobal(Integer year);
}