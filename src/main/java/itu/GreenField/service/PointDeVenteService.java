package itu.GreenField.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import itu.GreenField.model.MvtStockFille;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.model.Produit;
import itu.GreenField.repository.PointDeVenteRepository;
import itu.GreenField.repository.ProduitRepository;
@Service
public class PointDeVenteService {

    private PointDeVenteRepository pointDeVenteRepository;
    private ProduitRepository produitRepository;


    public PointDeVenteService(PointDeVenteRepository pointDeVenteRepository, ProduitRepository produitRepository) {
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.produitRepository = produitRepository;
    }

    public List<PointDeVente> getAllPointDeVente() {
        return pointDeVenteRepository.findAll();
    }

    public Optional<PointDeVente> getPointDeVenteById(Integer id) {
        return pointDeVenteRepository.findById(id);
    }

    public PointDeVente createPointDeVente(PointDeVente pointDeVente) {
        return pointDeVenteRepository.save(pointDeVente);
    }

    public PointDeVente updatePointDeVente(Integer id, PointDeVente pointDeVente) {
        Optional<PointDeVente> existingPointDeVente = pointDeVenteRepository.findById(id);
        if (existingPointDeVente.isPresent()) {
            PointDeVente updatedPointDeVente = existingPointDeVente.get();
            updatedPointDeVente.setNom(pointDeVente.getNom());
            updatedPointDeVente.setCode(pointDeVente.getCode());
            updatedPointDeVente.setAdresse(pointDeVente.getAdresse());
            updatedPointDeVente.setContact(pointDeVente.getContact());
            return pointDeVenteRepository.save(updatedPointDeVente);
        }
        return null;
    }

    public List<MvtStockFille> calculerStockDisponible(String codePointDeVente, LocalDateTime date, Integer idProduit) {
        List<Object[]> rawResults = pointDeVenteRepository.calculerStockDisponibleRaw(codePointDeVente, date, idProduit);
        List<MvtStockFille> stocks = new ArrayList<>();

        for (Object[] row : rawResults) {
            MvtStockFille stock = new MvtStockFille();
            if (row[0] != null) {
                // row[0] contains the product ID from the query result
                Integer produitId = ((Number) row[0]).intValue();
                Produit produit = produitRepository.findById(produitId).orElse(null);
                stock.setProduit(produit);
            }

            if (row[1] != null) {
                stock.setQuantite(((Number) row[1]).intValue());
            }
            stocks.add(stock);
        }
        return stocks;
    }

    public void deletePointDeVente(Integer id) {
        pointDeVenteRepository.deleteById(id);
    }
}
