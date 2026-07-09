package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import itu.greenField.service.CategorieProduitService;
import itu.greenField.service.FeatureService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.PubliciteService;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardClientController {

    private final ProduitService produitService;
    private final CategorieProduitService categorieService;
    private final FeatureService featureService;
    private final PubliciteService publiciteService;

    public DashboardClientController(ProduitService produitService, CategorieProduitService categorieService,
            FeatureService featureService, PubliciteService publiciteService) {
        this.produitService = produitService;
        this.categorieService = categorieService;
        this.featureService = featureService;
        this.publiciteService = publiciteService;
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
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("features", featureService.findAll());
        model.addAttribute("stats", featureService.findStats());
        model.addAttribute("publicites", publiciteService.findAll());

        return "front/accueil/acc2";
    }
}