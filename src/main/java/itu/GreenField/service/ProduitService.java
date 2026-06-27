package itu.GreenField.service;

import itu.GreenField.model.*;
import itu.GreenField.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieProduitRepository categorieProduitRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final MvtStockRepository mvtStockRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;

    public ProduitService(ProduitRepository produitRepository,
                          CategorieProduitRepository categorieProduitRepository,
                          PointDeVenteRepository pointDeVenteRepository,
                          MvtStockRepository mvtStockRepository,
                          MvtStockFilleRepository mvtStockFilleRepository) {
        this.produitRepository = produitRepository;
        this.categorieProduitRepository = categorieProduitRepository;
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.mvtStockRepository = mvtStockRepository;
        this.mvtStockFilleRepository = mvtStockFilleRepository;
    }

    public List<Produit> search(Integer idCategorie, String motCle) {
        return produitRepository.searchProduits(
                (idCategorie != null && idCategorie > 0) ? idCategorie : null,
                (motCle != null && !motCle.trim().isEmpty()) ? motCle.trim() : null
        );
    }

    public Optional<Produit> findById(Integer id) {
        return produitRepository.findById(id);
    }

    public List<CategorieProduit> findAllCategories() {
        return categorieProduitRepository.findAll();
    }

    public List<PointDeVente> findAllPointsDeVente() {
        return pointDeVenteRepository.findAll();
    }

    public Map<String, String> validateProduit(Produit produit) {
        Map<String, String> errors = new HashMap<>();

        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            errors.put("nom", "Le nom du produit est obligatoire.");
        }

        if (produit.getMatricule() == null || produit.getMatricule().trim().isEmpty()) {
            errors.put("matricule", "Le matricule est obligatoire.");
        } else {
            boolean exists = (produit.getId() == null) 
                    ? produitRepository.existsByMatricule(produit.getMatricule().trim())
                    : produitRepository.existsByMatriculeAndIdNot(produit.getMatricule().trim(), produit.getId());
            if (exists) {
                errors.put("matricule", "Ce matricule est déjà attribué.");
            }
        }

        if (produit.getPu() == null) {
            errors.put("pu", "Le prix unitaire est obligatoire.");
        } else if (produit.getPu().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("pu", "Le prix doit être strictement positif.");
        }

        if (produit.getCategorie() == null || produit.getCategorie().getId() == null) {
            errors.put("categorie", "La catégorie est obligatoire.");
        }

        return errors;
    }

    @Transactional
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    @Transactional
    public void delete(Integer id) {
        produitRepository.deleteById(id);
    }

    public Integer getStock(Integer idProduit, String ptDeVenteCode) {
        String code = (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) ? ptDeVenteCode.trim() : null;
        return mvtStockRepository.getStockByProduitAndOptionalPointDeVente(idProduit, code);
    }

    public List<ProduitStock> getStockLevels(Integer idCategorie, String motCle, String ptDeVenteCode) {
        List<Produit> produits = search(idCategorie, motCle);
        List<ProduitStock> stockList = new ArrayList<>();

        if (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) {
            Optional<PointDeVente> pdvOpt = pointDeVenteRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(ptDeVenteCode))
                    .findFirst();
            String pdvNom = pdvOpt.map(PointDeVente::getNom).orElse("Inconnu");

            for (Produit p : produits) {
                Integer qty = getStock(p.getId(), ptDeVenteCode);
                stockList.add(new ProduitStock(p, ptDeVenteCode, pdvNom, qty));
            }
        } else {
            for (Produit p : produits) {
                Integer qty = getStock(p.getId(), null);
                stockList.add(new ProduitStock(p, null, "Tous les points de vente", qty));
            }
        }

        return stockList;
    }

    @Transactional
    public void addStock(Integer idProduit, String ptDeVenteCode, TypeMvt typeMvt, Integer quantite) {
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive.");
        }

        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));

        PointDeVente pointDeVente = null;
        if (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) {
            pointDeVente = pointDeVenteRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(ptDeVenteCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Point de vente introuvable."));
        }

        MvtStock mvt = new MvtStock();
        mvt.setTypeMouvement(typeMvt);
        mvt.setPointDeVente(pointDeVente);
        mvtStockRepository.save(mvt);

        MvtStockFille mvtFille = new MvtStockFille();
        mvtFille.setMvtStock(mvt);
        mvtFille.setProduit(produit);
        mvtFille.setQuantite(quantite);
        mvtStockFilleRepository.save(mvtFille);
    }
}
