package itu.greenField.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return rechercherProduitsPage(idCategorie, motCle, Pageable.unpaged()).getContent();
    }

    public Page<Produit> rechercherProduitsPage(Integer idCategorie, String motCle, Pageable pageable) {
        boolean filtreCategorie = idCategorie != null;
        boolean filtreMotCle = motCle != null && !motCle.trim().isEmpty();
        String motCleNettoye = filtreMotCle ? motCle.trim() : null;

        if (filtreCategorie && filtreMotCle) {
            return produitRepository.findByCategorie_IdAndNomContainingIgnoreCase(idCategorie, motCleNettoye, pageable);
        }
        if (filtreCategorie) {
            return produitRepository.findByCategorie_Id(idCategorie, pageable);
        }
        if (filtreMotCle) {
            return produitRepository.findByNomContainingIgnoreCase(motCleNettoye, pageable);
        }
        return produitRepository.findAll(pageable);
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

    public List<Produit> bestSeller() {
        return produitRepository.findBestSellers(org.springframework.data.domain.PageRequest.of(0, 5));
    }

    public List<Produit> newProduit() {
        return produitRepository.findTop10ByOrderByIdDesc();
    }

    public int satisfaits() {
        return 100;
    }

    public int producteur() {
        return 2;
    }

    public int note() {
        return 3;
    }

    public double livraison() {
        return 8;
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Produit findProduitByMatricule(String matricule) {
        return produitRepository.findByMatricule(matricule).orElse(null);
    }

    public Produit findProduitByNom(String nom) {
        return produitRepository.findFirstByNom(nom).orElse(null);
    }

    public String produitToJson(Produit produit) {
        if (produit == null) {
            return "{\"id\": \"\", \"matricule\": \"\", \"nom\": \"\", \"pu\": \"0\"}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append("\"id\": \"" + produit.getId() + "\", ");
        sb.append("\"matricule\": \"" + produit.getMatricule() + "\", ");
        sb.append("\"nom\": \"" + produit.getNom() + "\", ");
        sb.append("\"pu\": \"" + produit.getPu() + "\"");

        sb.append("}");
        return sb.toString();
    }
}
