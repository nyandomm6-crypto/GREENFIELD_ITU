package itu.greenField.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Client;
import itu.greenField.model.Panier;
import itu.greenField.model.PanierFille;
import itu.greenField.model.Produit;
import itu.greenField.repository.PanierFilleRepository;
import itu.greenField.repository.PanierRepository;

@Service
public class PanierService {

    private final PanierRepository panierRepository;
    private final PanierFilleRepository panierFilleRepository;
    private final ProduitService produitService;

    public PanierService(PanierRepository panierRepository,
            PanierFilleRepository panierFilleRepository,
            ProduitService produitService) {
        this.panierRepository = panierRepository;
        this.panierFilleRepository = panierFilleRepository;
        this.produitService = produitService;
    }

    /**
     * Génère un nouveau token de session pour un panier anonyme.
     */
    public String genererToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Récupère le panier correspondant à un client connecté, ou en crée un
     * s'il n'en a pas encore.
     */
    @Transactional
    public Panier obtenirOuCreerPanierClient(Client client) {
        Panier panier = panierRepository.findByClient_Id(client.getId());
        if (panier != null) {
            return panier;
        }

        panier = new Panier();
        panier.setClient(client);
        panier.setDateCreation(LocalDateTime.now());
        return panierRepository.save(panier);
    }

    /**
     * Récupère le panier anonyme correspondant à un token de session, ou en
     * crée un nouveau si le token est inconnu ou absent.
     */
    @Transactional
    public Panier obtenirOuCreerPanierAnonyme(String tokenSession) {
        if (tokenSession != null) {
            Panier panier = panierRepository.findByTokenSession(tokenSession);
            if (panier != null) {
                return panier;
            }
        }

        Panier panier = new Panier();
        panier.setTokenSession(genererToken());
        panier.setDateCreation(LocalDateTime.now());
        return panierRepository.save(panier);
    }

    /**
     * Rattache un panier anonyme (token) à un client qui vient de se
     * connecter. Si le client possède déjà un panier, les lignes du panier
     * anonyme sont fusionnées dans son panier existant, puis le panier
     * anonyme est supprimé.
     */
    @Transactional
    public Panier rattacherAuClient(String tokenSession, Client client) {
        Panier panierClient = panierRepository.findByClient_Id(client.getId());
        Panier panierAnonyme = tokenSession == null ? null : panierRepository.findByTokenSession(tokenSession);

        if (panierAnonyme == null) {
            return panierClient != null ? panierClient : obtenirOuCreerPanierClient(client);
        }

        if (panierClient == null) {
            panierAnonyme.setClient(client);
            panierAnonyme.setTokenSession(null);
            return panierRepository.save(panierAnonyme);
        }

        // fusion des lignes du panier anonyme dans le panier du client
        for (PanierFille ligne : panierFilleRepository.findByPanier_Id(panierAnonyme.getId())) {
            ajouterAuPanier(panierClient, ligne.getProduit().getId(), ligne.getQuantite());
        }
        panierFilleRepository.deleteByPanier_Id(panierAnonyme.getId());
        panierRepository.delete(panierAnonyme);

        return panierClient;
    }

    public List<PanierFille> listerLignes(Panier panier) {
        return panierFilleRepository.findByPanier_Id(panier.getId());
    }

    public BigDecimal calculerTotal(Panier panier) {
        return listerLignes(panier).stream()
                .map(ligne -> ligne.getProduit().getPu().multiply(BigDecimal.valueOf(ligne.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Recherche le panier courant sans en créer si aucun n'existe (utilisé
     * pour l'affichage du badge dans la navigation, où créer un panier
     * vide à chaque visite serait superflu).
     */
    public Panier trouverPanierExistant(Client client, String tokenSession) {
        if (client != null) {
            return panierRepository.findByClient_Id(client.getId());
        }
        if (tokenSession != null) {
            return panierRepository.findByTokenSession(tokenSession);
        }
        return null;
    }

    public int compterArticles(Panier panier) {
        if (panier == null) {
            return 0;
        }
        return listerLignes(panier).stream()
                .mapToInt(PanierFille::getQuantite)
                .sum();
    }

    /**
     * Ajoute un produit au panier en s'assurant que la quantité demandée ne
     * dépasse pas le stock disponible.
     *
     * @return un message d'erreur si l'ajout est impossible, ou null si tout
     *         s'est bien passé.
     */
    @Transactional
    public String ajouterAuPanier(Panier panier, Integer idProduit, int quantite) {
        if (quantite <= 0) {
            return "La quantité doit être supérieure à zéro.";
        }

        Produit produit = produitService.trouverParId(idProduit);
        if (produit == null) {
            return "Produit introuvable.";
        }

        int stockDisponible = produitService.calculerStock(idProduit);

        PanierFille ligne = panierFilleRepository.findByPanier_IdAndProduit_Id(panier.getId(), idProduit);
        int dejaDansLePanier = ligne == null ? 0 : ligne.getQuantite();

        if (dejaDansLePanier + quantite > stockDisponible) {
            return "Stock insuffisant pour \"" + produit.getNom() + "\" (disponible : " + stockDisponible + ").";
        }

        if (ligne == null) {
            ligne = new PanierFille();
            ligne.setPanier(panier);
            ligne.setProduit(produit);
            ligne.setQuantite(quantite);
        } else {
            ligne.setQuantite(dejaDansLePanier + quantite);
        }
        panierFilleRepository.save(ligne);
        return null;
    }

    /**
     * Modifie la quantité d'une ligne du panier, en respectant le stock
     * disponible. Une quantité à zéro ou négative supprime la ligne.
     */
    @Transactional
    public String modifierQuantite(Panier panier, Integer idProduit, int quantite) {
        PanierFille ligne = panierFilleRepository.findByPanier_IdAndProduit_Id(panier.getId(), idProduit);
        if (ligne == null) {
            return null;
        }

        if (quantite <= 0) {
            panierFilleRepository.delete(ligne);
            return null;
        }

        int stockDisponible = produitService.calculerStock(idProduit);
        if (quantite > stockDisponible) {
            return "Stock insuffisant (disponible : " + stockDisponible + ").";
        }

        ligne.setQuantite(quantite);
        panierFilleRepository.save(ligne);
        return null;
    }

    @Transactional
    public void supprimerLigne(Panier panier, Integer idProduit) {
        PanierFille ligne = panierFilleRepository.findByPanier_IdAndProduit_Id(panier.getId(), idProduit);
        if (ligne != null) {
            panierFilleRepository.delete(ligne);
        }
    }

    @Transactional
    public void vider(Panier panier) {
        panierFilleRepository.deleteByPanier_Id(panier.getId());
    }
}
