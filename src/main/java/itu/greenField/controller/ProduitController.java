package itu.greenField.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Integer idCategorieValeur = (idCategorie == null || idCategorie.isBlank())
                ? null
                : Integer.valueOf(idCategorie);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<Produit> produitsPage = produitService.rechercherProduitsPage(idCategorieValeur, motCle, pageable);
        List<Produit> produits = produitsPage.getContent();
        List<CategorieProduit> categories = categorieProduitRepository.findAll();

        model.addAttribute("produits", produits);
        model.addAttribute("categories", categories);
        model.addAttribute("idCategorieSelectionnee", idCategorieValeur);
        model.addAttribute("motCle", motCle);
        model.addAttribute("currentPage", produitsPage.getNumber());
        model.addAttribute("totalPages", produitsPage.getTotalPages());
        model.addAttribute("totalElements", produitsPage.getTotalElements());
        model.addAttribute("pageSize", produitsPage.getSize());

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
