package itu.greenField.controller;

import itu.greenField.model.*;
import itu.greenField.service.ProduitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/back")
@RequiredArgsConstructor
public class ProduitBackController {

    private final ProduitService produitService;

    @GetMapping({ "/produits", "/produits/list" })
    public String listProduits(@RequestParam(required = false) Integer idCategorie,
            @RequestParam(required = false) String motCle,
            Model model) {
        List<Produit> produits = produitService.search(idCategorie, motCle);
        model.addAttribute("produits", produits);
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("selectedCategorie", idCategorie);
        model.addAttribute("motCle", motCle);
        return "front/produits/list";
    }

    @GetMapping("/produits/detail/{id}")
    public String detailProduit(@PathVariable Integer id, Model model) {
        Produit produit = produitService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + id));
        model.addAttribute("produit", produit);
        model.addAttribute("globalStock", produitService.getStock(id, null));

        List<PointDeVente> pdvs = produitService.findAllPointsDeVente();
        model.addAttribute("pdvs", pdvs);
        model.addAttribute("produitService", produitService);
        return "front/produits/detail";
    }

    @GetMapping("/produits/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("isEdit", false);
        return "front/produits/form";
    }

    @PostMapping("/produits/nouveau")
    public String processCreateForm(@ModelAttribute Produit produit,
            RedirectAttributes redirectAttributes,
            Model model) {
        Map<String, String> errors = produitService.validateProduit(produit);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("categories", produitService.findAllCategories());
            model.addAttribute("isEdit", false);
            return "front/produits/form";
        }
        produitService.save(produit);
        redirectAttributes.addFlashAttribute("success", "Produit créé avec succès.");
        return "redirect:/produits";
    }

    @GetMapping("/produits/modifier/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Produit produit = produitService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + id));
        model.addAttribute("produit", produit);
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("isEdit", true);
        return "front/produits/form";
    }

    @PostMapping("/produits/modifier/{id}")
    public String processEditForm(@PathVariable Integer id,
            @ModelAttribute Produit produit,
            RedirectAttributes redirectAttributes,
            Model model) {
        produit.setId(id);
        Map<String, String> errors = produitService.validateProduit(produit);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("categories", produitService.findAllCategories());
            model.addAttribute("isEdit", true);
            return "front/produits/form";
        }
        produitService.save(produit);
        redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès.");
        return "redirect:/produits";
    }

    @GetMapping("/produits/supprimer/{id}")
    public String deleteProduit(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            produitService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer ce produit.");
        }
        return "redirect:/produits";
    }

    @PostMapping(value = "/produits/validation", produces = "application/json")
    @ResponseBody
    public Map<String, String> validationProduitAsync(@RequestParam(required = false) Integer id,
            @RequestParam String nom,
            @RequestParam String matricule,
            @RequestParam Double pu,
            @RequestParam Integer idCategorie) {
        Produit p = new Produit();
        p.setId(id);
        p.setNom(nom);
        p.setMatricule(matricule);
        if (pu != null)
            p.setPu(java.math.BigDecimal.valueOf(pu));
        if (idCategorie != null && idCategorie > 0) {
            CategorieProduit cat = new CategorieProduit();
            cat.setId(idCategorie);
            p.setCategorie(cat);
        }
        return produitService.validateProduit(p);
    }

    @GetMapping("/stocks")
    public String listStocks(@RequestParam(required = false) Integer idCategorie,
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String ptDeVenteCode,
            Model model) {
        List<ProduitStock> stockLevels = produitService.getStockLevels(idCategorie, motCle, ptDeVenteCode);
        model.addAttribute("stocks", stockLevels);
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
        model.addAttribute("selectedCategorie", idCategorie);
        model.addAttribute("motCle", motCle);
        model.addAttribute("selectedPdv", ptDeVenteCode);
        return "front/produits/stock";
    }

    @GetMapping("/stocks/entree")
    public String showStockEntryForm(Model model) {
        model.addAttribute("produits", produitService.search(null, null));
        model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
        model.addAttribute("entryTypes", List.of(TypeMvt.Entree_Production, TypeMvt.Entree_Boutique));
        return "front/produits/stock-entry";
    }

    @PostMapping("/stocks/entree")
    public String processStockEntry(@RequestParam Integer idProduit,
            @RequestParam(required = false) String ptDeVenteCode,
            @RequestParam TypeMvt typeMouvement,
            @RequestParam Integer quantite,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (quantite == null || quantite <= 0) {
            model.addAttribute("error", "La quantité doit être strictement positive.");
            model.addAttribute("produits", produitService.search(null, null));
            model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
            model.addAttribute("entryTypes", List.of(TypeMvt.Entree_Production, TypeMvt.Entree_Boutique));
            return "front/produits/stock-entry";
        }

        try {
            produitService.addStock(idProduit, ptDeVenteCode, typeMouvement, quantite);
            redirectAttributes.addFlashAttribute("success", "Entrée de stock effectuée avec succès.");
            return "redirect:/stocks";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
            model.addAttribute("produits", produitService.search(null, null));
            model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
            model.addAttribute("entryTypes", List.of(TypeMvt.Entree_Production, TypeMvt.Entree_Boutique));
            return "front/produits/stock-entry";
        }
    }
}
