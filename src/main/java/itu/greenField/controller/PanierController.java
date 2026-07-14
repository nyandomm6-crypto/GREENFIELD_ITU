package itu.greenField.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Client;
import itu.greenField.model.Panier;
import itu.greenField.service.PanierService;

@Controller
public class PanierController {

    static final String CLE_SESSION_CLIENT = "client";
    static final String COOKIE_PANIER = "panierToken";
    private static final int DUREE_COOKIE_SECONDES = 60 * 60 * 24 * 30; // 30 jours

    private final PanierService panierService;

    public PanierController(PanierService panierService) {
        this.panierService = panierService;
    }

    /**
     * Récupère le panier courant : celui du client connecté s'il y en a un
     * en session, sinon le panier anonyme identifié par le cookie
     * "panierToken" (créé au besoin).
     */
    static Panier resoudrePanierCourant(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, PanierService panierService) {

        Client client = (Client) session.getAttribute(CLE_SESSION_CLIENT);
        if (client != null) {
            return panierService.obtenirOuCreerPanierClient(client);
        }

        String token = lireCookie(request, COOKIE_PANIER);
        Panier panier = panierService.obtenirOuCreerPanierAnonyme(token);

        if (token == null || !token.equals(panier.getTokenSession())) {
            ecrireCookie(response, COOKIE_PANIER, panier.getTokenSession());
        }

        return panier;
    }

    private static String lireCookie(HttpServletRequest request, String nom) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (nom.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static void ecrireCookie(HttpServletResponse response, String nom, String valeur) {
        Cookie cookie = new Cookie(nom, valeur);
        cookie.setPath("/");
        cookie.setMaxAge(DUREE_COOKIE_SECONDES);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @GetMapping("/panier")
    public String afficherPanier(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Model model) {

        Panier panier = resoudrePanierCourant(request, response, session, panierService);

        model.addAttribute("panier", panier);
        model.addAttribute("lignes", panierService.listerLignes(panier));
        model.addAttribute("total", panierService.calculerTotal(panier));
        model.addAttribute("poidsTotal", panierService.calculerPoidsTotal(panier));

        return "front/panier/panier";
    }

    // @PostMapping("/panier/ajouter")
    // public String ajouterAuPanier(
    // @RequestParam Integer idProduit,
    // @RequestParam(defaultValue = "1") int quantite,
    // HttpServletRequest request,
    // HttpServletResponse response,
    // HttpSession session,
    // RedirectAttributes redirectAttributes) {

    // Panier panier = resoudrePanierCourant(request, response, session,
    // panierService);
    // String erreur = panierService.ajouterAuPanier(panier, idProduit, quantite);

    // if (erreur != null) {
    // redirectAttributes.addFlashAttribute("error", erreur);
    // return "redirect:/produits/" + idProduit;
    // }

    // redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier.");
    // return "redirect:/panier";
    // }
    @PostMapping(value = "/panier/ajouter", produces = "application/json")
    @ResponseBody
    public Map<String, Object> ajouterAuPanier(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Récupérer les paramètres manuellement
            String idProduitStr = request.getParameter("idProduit");
            String quantiteStr = request.getParameter("quantite");

            System.out.println("=== AJOUT AU PANIER ===");
            System.out.println("idProduitStr: '" + idProduitStr + "'");
            System.out.println("quantiteStr: '" + quantiteStr + "'");

            // Vérifier l'ID du produit
            if (idProduitStr == null || idProduitStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "ID du produit manquant");
                return result;
            }

            // Parser l'ID du produit
            Integer idProduit;
            try {
                idProduit = Integer.parseInt(idProduitStr.trim());
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "ID du produit invalide: " + idProduitStr);
                return result;
            }

            // Parser la quantité (avec gestion des valeurs null/vides)
            int quantite = 1; // valeur par défaut
            if (quantiteStr != null && !quantiteStr.trim().isEmpty()) {
                try {
                    quantite = Integer.parseInt(quantiteStr.trim());
                    if (quantite < 1) {
                        quantite = 1; // quantité minimum
                    }
                } catch (NumberFormatException e) {
                    // Si la quantité est invalide, on garde 1
                    System.out.println("Quantité invalide, utilisation de 1 par défaut");
                }
            }

            System.out.println("idProduit parsé: " + idProduit);
            System.out.println("quantite parsée: " + quantite);

            // Récupérer le panier
            Panier panier = resoudrePanierCourant(request, response, session, panierService);
            System.out.println("Panier trouvé: " + (panier != null ? panier.getId() : "null"));

            // Ajouter au panier
            String erreur = panierService.ajouterAuPanier(panier, idProduit, quantite);
            System.out.println("Erreur: " + erreur);

            if (erreur != null) {
                result.put("success", false);
                result.put("message", erreur);
                return result;
            }

            // Calculer le nouveau total
            int nouveauTotal = panier.getLignes().stream()
                    .mapToInt(ligne -> ligne.getQuantite())
                    .sum();

            result.put("success", true);
            result.put("message", "Produit ajouté au panier");
            result.put("nouveauTotal", nouveauTotal);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
            System.err.println("Erreur détaillée: ");
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/panier/modifier")
    public String modifierQuantite(
            @RequestParam Integer idProduit,
            @RequestParam int quantite,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Panier panier = resoudrePanierCourant(request, response, session, panierService);
        String erreur = panierService.modifierQuantite(panier, idProduit, quantite);

        if (erreur != null) {
            redirectAttributes.addFlashAttribute("error", erreur);
        }

        return "redirect:/panier";
    }

    @PostMapping("/panier/supprimer/{idProduit}")
    public String supprimerDuPanier(@PathVariable Integer idProduit,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        Panier panier = resoudrePanierCourant(request, response, session, panierService);
        panierService.supprimerLigne(panier, idProduit);
        return "redirect:/panier";
    }
}
