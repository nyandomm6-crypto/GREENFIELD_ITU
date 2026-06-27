package itu.GreenField.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.GreenField.panier.Panier;
import itu.GreenField.service.PanierService;

@Controller
public class PanierController {

    private static final String CLE_SESSION_PANIER = "panier";

    private final PanierService panierService;

    public PanierController(PanierService panierService) {
        this.panierService = panierService;
    }

    private Panier obtenirPanier(HttpSession session) {
        Panier panier = (Panier) session.getAttribute(CLE_SESSION_PANIER);
        if (panier == null) {
            panier = new Panier();
            session.setAttribute(CLE_SESSION_PANIER, panier);
        }
        return panier;
    }

    @GetMapping("/panier")
    public String afficherPanier(HttpSession session, Model model) {
        model.addAttribute("panier", obtenirPanier(session));
        return "front/panier/panier";
    }

    @PostMapping("/panier/ajouter")
    public String ajouterAuPanier(
            @RequestParam Integer idProduit,
            @RequestParam(defaultValue = "1") Integer quantite,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Panier panier = obtenirPanier(session);
        String erreur = panierService.ajouterAuPanier(panier, idProduit, quantite);

        if (erreur != null) {
            redirectAttributes.addFlashAttribute("error", erreur);
            return "redirect:/produits/" + idProduit;
        }

        redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier.");
        return "redirect:/panier";
    }

    @PostMapping("/panier/modifier")
    public String modifierQuantite(
            @RequestParam Integer idProduit,
            @RequestParam Integer quantite,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Panier panier = obtenirPanier(session);
        String erreur = panierService.modifierQuantite(panier, idProduit, quantite);

        if (erreur != null) {
            redirectAttributes.addFlashAttribute("error", erreur);
        }

        return "redirect:/panier";
    }

    @PostMapping("/panier/supprimer/{idProduit}")
    public String supprimerDuPanier(@PathVariable Integer idProduit, HttpSession session) {
        Panier panier = obtenirPanier(session);
        panier.supprimerProduit(idProduit);
        return "redirect:/panier";
    }
}
