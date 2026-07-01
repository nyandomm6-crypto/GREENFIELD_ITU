package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.Produit;
import itu.greenField.repository.MvtStockFilleRepository;
import itu.greenField.repository.ProduitRepository;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;

    public ProduitService(ProduitRepository produitRepository,
            MvtStockFilleRepository mvtStockFilleRepository) {
        this.produitRepository = produitRepository;
        this.mvtStockFilleRepository = mvtStockFilleRepository;
    }

    public List<Produit> listerProduits(Integer idCategorie, String motCle) {
        boolean filtreCategorie = idCategorie != null;
        boolean filtreMotCle = motCle != null && !motCle.trim().isEmpty();

        if (filtreCategorie && filtreMotCle) {
            return produitRepository.findByCategorie_IdAndNomContainingIgnoreCase(idCategorie, motCle.trim());
        }
        if (filtreCategorie) {
            return produitRepository.findByCategorie_Id(idCategorie);
        }
        if (filtreMotCle) {
            return produitRepository.findByNomContainingIgnoreCase(motCle.trim());
        }
        return produitRepository.findAll();
    }

    public Produit trouverParId(Integer id) {
        return produitRepository.findById(id).orElse(null);
    }

    /**
     * Stock disponible (calculé) pour un produit donné, à partir des
     * mouvements de stock enregistrés.
     */
    public int calculerStock(Integer idProduit) {
        Integer stock = mvtStockFilleRepository.calculerStockProduit(idProduit);
        return stock == null ? 0 : stock;
    }
}
