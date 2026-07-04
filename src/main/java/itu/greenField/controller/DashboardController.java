package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import itu.greenField.service.ProduitService;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private ProduitService produitService;

    public DashboardController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping("/")
    public String accueil(HttpSession session, Model model) {
        // L'utilisateur est déjà dans la session si connecté
        // via votre système d'authentification
        model.addAttribute("bestSellers", produitService.bestSeller());
        model.addAttribute("newProduits", produitService.newProduit());
        model.addAttribute("satisfaits", produitService.satisfaits());
        model.addAttribute("producteur", produitService.producteur());
        model.addAttribute("livraison", produitService.livraison());
        model.addAttribute("note", produitService.note());

        return "front/accueil/accueil";
    }
}