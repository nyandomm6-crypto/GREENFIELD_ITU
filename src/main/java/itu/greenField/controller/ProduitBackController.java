package itu.greenField.controller;

import itu.greenField.model.*;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.ProduitBackService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/back")
public class ProduitBackController {

    private final ProduitBackService produitService;

    /** Garde de rôle : toutes les routes /back de ce contrôleur sont réservées à l'Administrateur. */
    @ModelAttribute
    public void guardAdmin(HttpSession session) {
        if (!AuthGuard.isAdmin(session)) {
            throw new AuthGuard.AccesRefuseException();
        }
    }

    @ExceptionHandler(AuthGuard.AccesRefuseException.class)
    public String onAccesRefuse() {
        return "redirect:/emp/login";
    }

    @GetMapping({ "/produits", "/produits/list" })
    public String listProduits(@RequestParam(required = false) Integer idCategorie,
            @RequestParam(required = false) String motCle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Produit> produitsPage = produitService.searchPage(idCategorie, motCle, page, size);
        model.addAttribute("produits", produitsPage.getContent());
        model.addAttribute("page", produitsPage.getNumber());
        model.addAttribute("totalPages", produitsPage.getTotalPages());
        model.addAttribute("totalElements", produitsPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("selectedCategorie", idCategorie);
        model.addAttribute("motCle", motCle);
        return "back/produits/list";
    }

    // ==================== EXCEL ====================

    @GetMapping("/produits/export")
    public ResponseEntity<byte[]> exportProduits() throws Exception {
        byte[] content = produitService.exportExcel();
        return excelResponse(content, "produits.xlsx");
    }

    @GetMapping("/produits/template")
    public ResponseEntity<byte[]> templateProduits() throws Exception {
        byte[] content = produitService.templateExcel();
        return excelResponse(content, "modele_produits.xlsx");
    }

    @GetMapping("/produits/import")
    public String showImportForm() {
        return "back/produits/importExcel";
    }

    @PostMapping("/produits/import")
    public String importProduits(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty() || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un fichier Excel valide (.xlsx).");
            return "redirect:/back/produits/import";
        }
        try {
            int count = produitService.importExcel(file.getInputStream());
            redirectAttributes.addFlashAttribute("success", count + " produit(s) importé(s) avec succès.");
            return "redirect:/back/produits";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation : " + e.getMessage());
            return "redirect:/back/produits/import";
        }
    }

    private ResponseEntity<byte[]> excelResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
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
        return "back/produits/detail";
    }

    @GetMapping("/produits/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("isEdit", false);
        return "back/produits/form";
    }

    @PostMapping("/produits/nouveau")
    public String processCreateForm(@ModelAttribute Produit produit,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes,
            Model model) {
        Map<String, String> errors = produitService.validateProduit(produit);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("categories", produitService.findAllCategories());
            model.addAttribute("isEdit", false);
            return "back/produits/form";
        }
        produitService.saveWithImage(produit, image);
        redirectAttributes.addFlashAttribute("success", "Produit créé avec succès.");
        return "redirect:/back/produits";
    }

    @GetMapping("/produits/modifier/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Produit produit = produitService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + id));
        model.addAttribute("produit", produit);
        model.addAttribute("categories", produitService.findAllCategories());
        model.addAttribute("isEdit", true);
        return "back/produits/form";
    }

    @PostMapping("/produits/modifier/{id}")
    public String processEditForm(@PathVariable Integer id,
            @ModelAttribute Produit produit,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes,
            Model model) {
        produit.setId(id);
        Map<String, String> errors = produitService.validateProduit(produit);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("categories", produitService.findAllCategories());
            model.addAttribute("isEdit", true);
            return "back/produits/form";
        }
        produitService.saveWithImage(produit, image);
        redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès.");
        return "redirect:/back/produits";
    }

    @GetMapping("/produits/supprimer/{id}")
    public String deleteProduit(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            produitService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer ce produit.");
        }
        return "redirect:/back/produits";
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
        return "back/produits/stock";
    }

    @GetMapping("/stocks/entree")
    public String showStockEntryForm(Model model) {
        model.addAttribute("produits", produitService.search(null, null));
        model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
        model.addAttribute("entryTypes", List.of(TypeMvt.Entree_Production, TypeMvt.Entree_Boutique));
        return "back/produits/stock-entry";
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
            return "back/produits/stock-entry";
        }

        try {
            produitService.addStock(idProduit, ptDeVenteCode, typeMouvement, quantite);
            redirectAttributes.addFlashAttribute("success", "Entrée de stock effectuée avec succès.");
            return "redirect:/back/stocks";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
            model.addAttribute("produits", produitService.search(null, null));
            model.addAttribute("pointsDeVente", produitService.findAllPointsDeVente());
            model.addAttribute("entryTypes", List.of(TypeMvt.Entree_Production, TypeMvt.Entree_Boutique));
            return "back/produits/stock-entry";
        }
    }
}
