package itu.greenField.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.ModeReception;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.Panier;
import itu.greenField.model.PanierFille;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.DetailsCommandeRepository;
import itu.greenField.repository.PointDeVenteRepository;
import itu.greenField.repository.StatutCommandeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandeFrontService {

    private final CommandesRepository commandesRepository;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final ProduitService produitService;
    private final PanierService panierService;
    private final StatutCommandeRepository statutCommandeRepository;

    /**
     * Valide l'achat : vérifie une dernière fois le stock de chaque ligne,
     * puis crée la commande et ses détails. Le prix unitaire est figé au
     * moment de l'achat (pu_au_moment_achat), conformément aux règles
     * d'historisation du projet. Le panier est vidé une fois la commande
     * créée.
     *
     * @return null si la commande a été créée, sinon un message d'erreur.
     */
    @Transactional
    public String validerAchat(Panier panier, Client client, ModeReception mode, String adresse, String point,
            LocalDateTime dateHeure, AtomicReference<Integer> idCommande) {
        List<PanierFille> lignes = panierService.listerLignes(panier);
        StatutCommande statut = statutCommandeRepository.findByNom("Créée")
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        if (lignes.isEmpty()) {
            return "Le panier est vide.";
        }

        if (mode == ModeReception.Livraison_Domicile && (adresse == null || adresse.isBlank())) {
            return "L'adresse de livraison est obligatoire pour une livraison à domicile.";
        }

        if (mode == ModeReception.Retrait_Boutique && (point == null || point.isBlank())) {
            return "Le point de retrait est obligatoire pour un retrait en boutique.";
        }

        for (PanierFille ligne : lignes) {
            int stockDisponible = produitService.calculerStock(ligne.getProduit().getId());
            if (ligne.getQuantite() > stockDisponible) {
                return "Stock insuffisant pour \"" + ligne.getProduit().getNom()
                        + "\" (disponible : " + stockDisponible + ").";
            }
        }

        BigDecimal total = panierService.calculerTotal(panier);
        int totalProduits = lignes.stream()
                .filter(Objects::nonNull)
                .mapToInt(ligne -> ligne.getQuantite())
                .sum();

        Commandes commande = new Commandes();
        commande.setClient(client);
        commande.setDatecommande(Timestamp.valueOf(LocalDateTime.now()));
        commande.setModeReception(mode);
        commande.setTypeCommande(mode == ModeReception.Livraison_Domicile
                ? itu.greenField.model.TypeCommande.En_ligne
                : itu.greenField.model.TypeCommande.En_boutique);
        commande.setFraisLivraison(BigDecimal.ZERO);
        commande.setTotalProduits(totalProduits);
        commande.setTotalGeneral(total);

        if (dateHeure != null) {
            Timestamp reception = Timestamp.valueOf(dateHeure);
            commande.setHeureReceptionDebut(reception);
            commande.setHeureReceptionFin(reception);
        }

        if (mode == ModeReception.Livraison_Domicile) {
            commande.setAdresseLivraison(adresse.trim());
        } else {
            PointDeVente pointDeVente = resoudrePointDeVenteRetrait(point);
            if (pointDeVente == null) {
                return "Le point de retrait sélectionné est introuvable.";
            }
            commande.setPointDeVenteRetrait(pointDeVente);
        }
        commande.setStatutActuel(statut);

        commande = commandesRepository.save(commande);
        idCommande.set(commande.getId());

        for (PanierFille ligne : lignes) {
            DetailsCommande detail = new DetailsCommande();
            detail.setCommande(commande);
            detail.setProduit(ligne.getProduit());
            detail.setQuantite(ligne.getQuantite());
            detail.setPuAuMomentAchat(ligne.getProduit().getPu());
            detailsCommandeRepository.save(detail);
        }

        panierService.vider(panier);
        return null;
    }

    private PointDeVente resoudrePointDeVenteRetrait(String point) {
        if (point == null || point.isBlank()) {
            return null;
        }

        String valeurRecherchee = point.trim().toLowerCase();

        return pointDeVenteRepository.findAll().stream()
                .filter(pointDeVente -> correspond(pointDeVente.getCode(), valeurRecherchee)
                        || correspond(pointDeVente.getNom(), valeurRecherchee)
                        || correspond(pointDeVente.getAdresse(), valeurRecherchee))
                .findFirst()
                .orElse(null);
    }

    private boolean correspond(String valeurBase, String valeurRecherchee) {
        if (valeurBase == null) {
            return false;
        }

        String normalisee = valeurBase.trim().toLowerCase();
        return normalisee.equals(valeurRecherchee) || normalisee.contains(valeurRecherchee);
    }

    public Page<Commandes> findByClient(Client client, String motCle, String statut, Pageable pageable) {
        return commandesRepository.findByClientWithFiltre(client, motCle, statut, pageable);
    }

    public List<Commandes> findByClient(Client client) {
        return commandesRepository.findByClient(client);
    }

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findAll();
    }

}
