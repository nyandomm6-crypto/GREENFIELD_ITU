package itu.greenfield.service;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;

import java.util.List;

public interface StatistiqueService {
    List<ProduitStatDto> getTop5Produits();
    List<Produit> getNouveauxProduits();
    Double getBeneficeFromage();
    List<EvolutionVenteDto> getEvolutionVentes();
    List<ClientStatDto> getTop5Clients();
}