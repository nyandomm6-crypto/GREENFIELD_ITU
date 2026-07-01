package itu.GreenField.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import itu.GreenField.model.Client;
import itu.GreenField.model.Panier;
import itu.GreenField.service.PanierService;

/**
 * Rend disponible, dans toutes les vues, le nombre d'articles du panier
 * courant (client connecté ou panier anonyme via cookie), afin d'afficher
 * le badge dans la barre de navigation sans dupliquer cette logique dans
 * chaque contrôleur. Lecture seule : ne crée ni panier ni cookie.
 */
@ControllerAdvice
public class NavigationControllerAdvice {

    private final PanierService panierService;

    public NavigationControllerAdvice(PanierService panierService) {
        this.panierService = panierService;
    }

    @ModelAttribute("nombreArticlesPanier")
    public int nombreArticlesPanier(HttpServletRequest request, HttpSession session) {
        Client client = (Client) session.getAttribute(PanierController.CLE_SESSION_CLIENT);

        String token = null;
        if (client == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (PanierController.COOKIE_PANIER.equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        Panier panier = panierService.trouverPanierExistant(client, token);
        return panierService.compterArticles(panier);
    }
}
