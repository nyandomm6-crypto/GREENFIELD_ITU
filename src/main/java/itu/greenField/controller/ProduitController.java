package itu.greenField.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.CategorieProduit;
import itu.greenField.model.Client;
import itu.greenField.model.Produit;
import itu.greenField.repository.CategorieProduitRepository;
import itu.greenField.service.AvisProduitService;
import itu.greenField.service.ProduitService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieProduitRepository categorieProduitRepository;
    private final AvisProduitService avisProduitService;

    public ProduitController(ProduitService produitService,
            CategorieProduitRepository categorieProduitRepository,
            AvisProduitService avisProduitService) {
        this.produitService = produitService;
        this.categorieProduitRepository = categorieProduitRepository;
        this.avisProduitService = avisProduitService;
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
        model.addAttribute("avis", avisProduitService.listerParProduit(produit));

        return "front/produits/detail";
    }

    @PostMapping("/produits/{id}/avis")
    public String posterAvis(@PathVariable Integer id,
            @RequestParam("commentaire") String commentaire,
            @RequestParam(value = "note", defaultValue = "5") Integer note,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Client client = (Client) session.getAttribute(PanierController.CLE_SESSION_CLIENT);
        if (client == null) {
            return "redirect:/login?redirect=/produits/" + id;
        }

        Produit produit = produitService.trouverParId(id);
        if (produit == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/produits";
        }

        Integer noteValide = note == null ? 5 : Math.max(1, Math.min(5, note));
        String nomClient = client.getNom() + " " + client.getPrenom();
        avisProduitService.enregistrerAvis(produit, client, nomClient, noteValide, commentaire);
        redirectAttributes.addFlashAttribute("success", "Votre avis a bien été enregistré.");
        return "redirect:/produits/" + id;
    }

    @GetMapping(value = "/produits/{matricule}/detail-json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getProduit(@PathVariable String matricule) {
        return produitService.produitToJson(produitService.findProduitByMatricule(matricule));
    }
}
