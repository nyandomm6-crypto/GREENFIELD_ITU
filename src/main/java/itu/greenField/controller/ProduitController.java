package itu.greenField.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.CategorieProduit;
import itu.greenField.model.Produit;
import itu.greenField.repository.CategorieProduitRepository;
import itu.greenField.service.ProduitService;

@Controller
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieProduitRepository categorieProduitRepository;

    public ProduitController(ProduitService produitService,
            CategorieProduitRepository categorieProduitRepository) {
        this.produitService = produitService;
        this.categorieProduitRepository = categorieProduitRepository;
    }

    @GetMapping("/produits")
    public String listerProduits(
            @RequestParam(required = false) String idCategorie,
            @RequestParam(required = false) String motCle,
            Model model) {

        Integer idCategorieValeur = (idCategorie == null || idCategorie.isBlank())
                ? null
                : Integer.valueOf(idCategorie);

        List<Produit> produits = produitService.listerProduits(idCategorieValeur, motCle);
        List<CategorieProduit> categories = categorieProduitRepository.findAll();

        model.addAttribute("produits", produits);
        model.addAttribute("categories", categories);
        model.addAttribute("idCategorieSelectionnee", idCategorieValeur);
        model.addAttribute("motCle", motCle);

        // on attache le stock calculé à chaque produit pour l'affichage
        model.addAttribute("stocks", produits.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Produit::getId,
                        p -> produitService.calculerStock(p.getId()))));

        return "front/produits/liste";
    }

    @GetMapping("/produits/{id}")
    public String detailProduit(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Produit produit = produitService.trouverParId(id);

        if (produit == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/produits";
        }

        model.addAttribute("produit", produit);
        model.addAttribute("stock", produitService.calculerStock(id));

        return "front/produits/detail";
    }

    @GetMapping(value = "/produits/{matricule}/detail-json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getProduit(@PathVariable String matricule) {
        return produitService.produitToJson(produitService.findProduitByMatricule(matricule));
    }
}
