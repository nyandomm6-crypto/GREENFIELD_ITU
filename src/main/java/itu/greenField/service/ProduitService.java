package itu.greenfield.service;

import org.springframework.stereotype.Service;

import itu.greenfield.model.Produit;
import itu.greenfield.repository.ProduitRepository;

import java.util.List;

@Service
public class ProduitService {
    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
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
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append("\"id\": \""+produit.getId()+"\", ");
        sb.append("\"matricule\": \""+produit.getMatricule()+"\", ");
        sb.append("\"nom\": \""+produit.getNom()+"\", ");
        sb.append("\"pu\": \""+produit.getPu()+"\"");

        sb.append("}");
        return sb.toString();
    }
}
