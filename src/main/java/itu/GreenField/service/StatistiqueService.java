package com.greenfield.service;

import com.greenfield.dto.ClientStatDto;
import com.greenfield.dto.EvolutionVenteDto;
import com.greenfield.dto.ProduitStatDto;
import com.greenfield.model.Produit;

import java.util.List;

public interface StatistiqueService {
    List<ProduitStatDto> getTop5Produits();
    List<Produit> getNouveauxProduits();
    Double getBeneficeFromage();
    List<EvolutionVenteDto> getEvolutionVentes();
    List<ClientStatDto> getTop5Clients();
}