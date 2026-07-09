package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.model.AvisProduit;
import itu.greenField.model.Client;
import itu.greenField.model.Produit;
import itu.greenField.repository.AvisProduitRepository;

@Service
public class AvisProduitService {

    private final AvisProduitRepository avisProduitRepository;

    public AvisProduitService(AvisProduitRepository avisProduitRepository) {
        this.avisProduitRepository = avisProduitRepository;
    }

    public AvisProduit enregistrerAvis(Produit produit, Client client, String nomClient, Integer note,
            String commentaire) {
        AvisProduit avis = new AvisProduit();
        avis.setProduit(produit);
        avis.setClient(client);
        avis.setNomClient(nomClient);
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        return avisProduitRepository.save(avis);
    }

    public List<AvisProduit> listerParProduit(Produit produit) {
        return avisProduitRepository.findByProduitOrderByDateCreationDesc(produit);
    }
}
