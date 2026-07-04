package itu.greenField.service;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;
import itu.greenField.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Override
    public List<ProduitStatDto> getTop5Produits() {
        return statistiqueRepository.findTop5ProduitsPlusVendus();
    }

    @Override
    public List<Produit> getNouveauxProduits() {
        return statistiqueRepository.findNouveauxProduits();
    }

    @Override
    public Double getBeneficeFromage() {
        // Recherche automatique pour le libellé contenant "fromage"
        return statistiqueRepository.getChiffreAffairesParCategorie("fromage");
    }

    @Override
    public List<EvolutionVenteDto> getEvolutionVentes() {
        return statistiqueRepository.findEvolutionDesVentes();
    }

    @Override
    public List<ClientStatDto> getTop5Clients() {
        return statistiqueRepository.findTop5MeilleursClients();
    }
}