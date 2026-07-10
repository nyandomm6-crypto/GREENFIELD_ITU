package itu.greenField.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.CategorieProduit;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.CategorieProduitService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/back/categories")
public class CategorieProduitController {

    private final CategorieProduitService categorieService;

    /**
     * Empêche le data-binder de mapper le fichier « image » (MultipartFile) sur la
     * propriété String CategorieProduit.image — le fichier est récupéré via @RequestParam.
     */
    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setDisallowedFields("image");
    }

    /** Garde de rôle : /back/categories réservé à l'Administrateur. */
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

    @GetMapping({ "", "/", "/list" })
    public String list(@RequestParam(required = false) String motCle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CategorieProduit> categoriesPage = categorieService.searchPage(motCle, page, size);
        model.addAttribute("categories", categoriesPage.getContent());
        model.addAttribute("page", categoriesPage.getNumber());
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("totalElements", categoriesPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("motCle", motCle);
        return "back/categories/list";
    }

    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("categorie", new CategorieProduit());
        model.addAttribute("isEdit", false);
        return "back/categories/form";
    }

    @PostMapping("/nouveau")
    public String create(@ModelAttribute CategorieProduit categorie,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        String error = categorieService.validate(categorie);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("isEdit", false);
            return "back/categories/form";
        }
        categorieService.save(categorie, image);
        redirectAttributes.addFlashAttribute("success", "Catégorie créée avec succès.");
        return "redirect:/back/categories";
    }

    @GetMapping("/modifier/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        CategorieProduit categorie = categorieService.findById(id).orElse(null);
        if (categorie == null) {
            redirectAttributes.addFlashAttribute("error", "Catégorie introuvable.");
            return "redirect:/back/categories";
        }
        model.addAttribute("categorie", categorie);
        model.addAttribute("isEdit", true);
        return "back/categories/form";
    }

    @PostMapping("/modifier/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute CategorieProduit categorie,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        categorie.setId(id);
        String error = categorieService.validate(categorie);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("isEdit", true);
            return "back/categories/form";
        }
        categorieService.save(categorie, image);
        redirectAttributes.addFlashAttribute("success", "Catégorie modifiée avec succès.");
        return "redirect:/back/categories";
    }

    @GetMapping("/supprimer/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Catégorie supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Impossible de supprimer cette catégorie (des produits y sont peut-être rattachés).");
        }
        return "redirect:/back/categories";
    }

    // ==================== EXCEL ====================

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() throws Exception {
        return excelResponse(categorieService.exportExcel(), "categories.xlsx");
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> template() throws Exception {
        return excelResponse(categorieService.templateExcel(), "modele_categories.xlsx");
    }

    @GetMapping("/import")
    public String showImportForm() {
        return "back/categories/importExcel";
    }

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty() || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un fichier Excel valide (.xlsx).");
            return "redirect:/back/categories/import";
        }
        try {
            int count = categorieService.importExcel(file.getInputStream());
            redirectAttributes.addFlashAttribute("success", count + " catégorie(s) importée(s) avec succès.");
            return "redirect:/back/categories";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation : " + e.getMessage());
            return "redirect:/back/categories/import";
        }
    }

    private ResponseEntity<byte[]> excelResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
