package itu.greenfield.service;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;

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