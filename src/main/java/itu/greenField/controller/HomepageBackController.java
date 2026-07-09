package itu.greenField.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Banniere;
import itu.greenField.model.Faq;
import itu.greenField.model.Feature;
import itu.greenField.model.Publicite;
import itu.greenField.model.Temoignage;
import itu.greenField.service.BanniereService;
import itu.greenField.service.FaqService;
import itu.greenField.service.FeatureService;
import itu.greenField.service.PubliciteService;
import itu.greenField.service.TemoignageService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/back/homepage")
public class HomepageBackController {

    private final FeatureService featureService;
    private final PubliciteService publiciteService;
    private final BanniereService banniereService;
    private final FaqService faqService;
    private final TemoignageService temoignageService;

    @GetMapping({ "", "/", "/list" })
    public String list(Model model) {
        model.addAttribute("features", featureService.findAll());
        model.addAttribute("stats", featureService.findStats());
        model.addAttribute("publicites", publiciteService.findAll());
        model.addAttribute("bannieres", banniereService.findAll());
        model.addAttribute("faqList", faqService.findAll());
        model.addAttribute("temoignages", temoignageService.findAll());
        return "back/homepage/list";
    }

    @GetMapping("/features/nouveau")
    public String newFeature(Model model) {
        Feature feature = new Feature();
        feature.setSection("features");
        model.addAttribute("feature", feature);
        model.addAttribute("isEdit", false);
        model.addAttribute("statMode", false);
        return "back/homepage/form-feature";
    }

    @PostMapping("/features/nouveau")
    public String createFeature(@ModelAttribute Feature feature, Model model, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = validateFeature(feature);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            model.addAttribute("statMode", false);
            return "back/homepage/form-feature";
        }
        featureService.save(feature);
        redirectAttributes.addFlashAttribute("success", "Feature ajoutée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/features/modifier/{id}")
    public String editFeature(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Feature feature = featureService.findById(id);
        if (feature == null) {
            redirectAttributes.addFlashAttribute("error", "Élément introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("feature", feature);
        model.addAttribute("isEdit", true);
        model.addAttribute("statMode", false);
        return "back/homepage/form-feature";
    }

    @PostMapping("/features/modifier/{id}")
    public String updateFeature(@PathVariable Long id, @ModelAttribute Feature feature, Model model,
            RedirectAttributes redirectAttributes) {
        feature.setId(id);
        Map<String, String> errors = validateFeature(feature);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            model.addAttribute("statMode", false);
            return "back/homepage/form-feature";
        }
        featureService.save(feature);
        redirectAttributes.addFlashAttribute("success", "Feature modifiée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/features/supprimer/{id}")
    public String deleteFeature(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        featureService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Feature supprimée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/stats/nouveau")
    public String newStat(Model model) {
        Feature feature = new Feature();
        feature.setSection("stats");
        model.addAttribute("feature", feature);
        model.addAttribute("isEdit", false);
        model.addAttribute("statMode", true);
        return "back/homepage/form-feature";
    }

    @PostMapping("/stats/nouveau")
    public String createStat(@ModelAttribute Feature feature, Model model, RedirectAttributes redirectAttributes) {
        feature.setSection("stats");
        Map<String, String> errors = validateFeature(feature);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            model.addAttribute("statMode", true);
            return "back/homepage/form-feature";
        }
        featureService.save(feature);
        redirectAttributes.addFlashAttribute("success", "Statistique ajoutée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/stats/modifier/{id}")
    public String editStat(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Feature feature = featureService.findById(id);
        if (feature == null) {
            redirectAttributes.addFlashAttribute("error", "Élément introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("feature", feature);
        model.addAttribute("isEdit", true);
        model.addAttribute("statMode", true);
        return "back/homepage/form-feature";
    }

    @PostMapping("/stats/modifier/{id}")
    public String updateStat(@PathVariable Long id, @ModelAttribute Feature feature, Model model,
            RedirectAttributes redirectAttributes) {
        feature.setId(id);
        feature.setSection("stats");
        Map<String, String> errors = validateFeature(feature);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            model.addAttribute("statMode", true);
            return "back/homepage/form-feature";
        }
        featureService.save(feature);
        redirectAttributes.addFlashAttribute("success", "Statistique modifiée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/stats/supprimer/{id}")
    public String deleteStat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        featureService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Statistique supprimée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/publicites/nouveau")
    public String newPublicite(Model model) {
        model.addAttribute("publicite", new Publicite());
        model.addAttribute("isEdit", false);
        return "back/homepage/form-publicite";
    }

    @PostMapping("/publicites/nouveau")
    public String createPublicite(@ModelAttribute Publicite publicite,
            @RequestParam(required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = validatePublicite(publicite);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            return "back/homepage/form-publicite";
        }
        publiciteService.saveWithImage(publicite, image);
        redirectAttributes.addFlashAttribute("success", "Publicité ajoutée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/publicites/modifier/{id}")
    public String editPublicite(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Publicite publicite = publiciteService.findById(id);
        if (publicite == null) {
            redirectAttributes.addFlashAttribute("error", "Élément introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("publicite", publicite);
        model.addAttribute("isEdit", true);
        return "back/homepage/form-publicite";
    }

    @PostMapping("/publicites/modifier/{id}")
    public String updatePublicite(@PathVariable Long id, @ModelAttribute Publicite publicite,
            @RequestParam(required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        publicite.setId(id);
        Map<String, String> errors = validatePublicite(publicite);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            return "back/homepage/form-publicite";
        }
        publiciteService.saveWithImage(publicite, image);
        redirectAttributes.addFlashAttribute("success", "Publicité modifiée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/publicites/supprimer/{id}")
    public String deletePublicite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        publiciteService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Publicité supprimée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/bannieres/nouveau")
    public String newBanniere(Model model) {
        model.addAttribute("banniere", new Banniere());
        model.addAttribute("isEdit", false);
        return "back/homepage/form-banniere";
    }

    @PostMapping("/bannieres/nouveau")
    public String createBanniere(@ModelAttribute Banniere banniere,
            @RequestParam(required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = validateBanniere(banniere);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            return "back/homepage/form-banniere";
        }
        banniereService.saveWithImage(banniere, image);
        redirectAttributes.addFlashAttribute("success", "Bannière ajoutée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/bannieres/modifier/{id}")
    public String editBanniere(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Banniere banniere = banniereService.findById(id);
        if (banniere == null) {
            redirectAttributes.addFlashAttribute("error", "Élément introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("banniere", banniere);
        model.addAttribute("isEdit", true);
        return "back/homepage/form-banniere";
    }

    @PostMapping("/bannieres/modifier/{id}")
    public String updateBanniere(@PathVariable Long id, @ModelAttribute Banniere banniere,
            @RequestParam(required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {
        banniere.setId(id);
        Map<String, String> errors = validateBanniere(banniere);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            return "back/homepage/form-banniere";
        }
        banniereService.saveWithImage(banniere, image);
        redirectAttributes.addFlashAttribute("success", "Bannière modifiée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/bannieres/supprimer/{id}")
    public String deleteBanniere(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        banniereService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Bannière supprimée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/faq/nouveau")
    public String newFaq(Model model) {
        model.addAttribute("faq", new Faq());
        model.addAttribute("isEdit", false);
        return "back/homepage/form-faq";
    }

    @PostMapping("/faq/nouveau")
    public String createFaq(@ModelAttribute Faq faq, Model model, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = validateFaq(faq);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            return "back/homepage/form-faq";
        }
        faqService.save(faq);
        redirectAttributes.addFlashAttribute("success", "FAQ ajoutée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/faq/modifier/{id}")
    public String editFaq(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Faq faq = faqService.findById(id);
        if (faq == null) {
            redirectAttributes.addFlashAttribute("error", "FAQ introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("faq", faq);
        model.addAttribute("isEdit", true);
        return "back/homepage/form-faq";
    }

    @PostMapping("/faq/modifier/{id}")
    public String updateFaq(@PathVariable Integer id, @ModelAttribute Faq faq, Model model,
            RedirectAttributes redirectAttributes) {
        faq.setId(id);
        Map<String, String> errors = validateFaq(faq);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            return "back/homepage/form-faq";
        }
        faqService.save(faq);
        redirectAttributes.addFlashAttribute("success", "FAQ modifiée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/faq/supprimer/{id}")
    public String deleteFaq(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        faqService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "FAQ supprimée avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/temoignages/nouveau")
    public String newTemoignage(Model model) {
        model.addAttribute("temoignage", new Temoignage());
        model.addAttribute("isEdit", false);
        return "back/homepage/form-temoignage";
    }

    @PostMapping("/temoignages/nouveau")
    public String createTemoignage(@ModelAttribute Temoignage temoignage, Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = validateTemoignage(temoignage);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", false);
            return "back/homepage/form-temoignage";
        }
        temoignageService.save(temoignage);
        redirectAttributes.addFlashAttribute("success", "Témoignage ajouté avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/temoignages/modifier/{id}")
    public String editTemoignage(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Temoignage temoignage = temoignageService.findById(id);
        if (temoignage == null) {
            redirectAttributes.addFlashAttribute("error", "Témoignage introuvable.");
            return "redirect:/back/homepage";
        }
        model.addAttribute("temoignage", temoignage);
        model.addAttribute("isEdit", true);
        return "back/homepage/form-temoignage";
    }

    @PostMapping("/temoignages/modifier/{id}")
    public String updateTemoignage(@PathVariable Integer id, @ModelAttribute Temoignage temoignage, Model model,
            RedirectAttributes redirectAttributes) {
        temoignage.setId(id);
        Map<String, String> errors = validateTemoignage(temoignage);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("isEdit", true);
            return "back/homepage/form-temoignage";
        }
        temoignageService.save(temoignage);
        redirectAttributes.addFlashAttribute("success", "Témoignage modifié avec succès.");
        return "redirect:/back/homepage";
    }

    @GetMapping("/temoignages/supprimer/{id}")
    public String deleteTemoignage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        temoignageService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Témoignage supprimé avec succès.");
        return "redirect:/back/homepage";
    }

    private Map<String, String> validateFeature(Feature feature) {
        Map<String, String> errors = new HashMap<>();
        if (feature.getIcon() == null || feature.getIcon().trim().isEmpty()) {
            errors.put("icon", "L'icône est requise.");
        }
        if (feature.getTitre() == null || feature.getTitre().trim().isEmpty()) {
            errors.put("titre", "Le titre est requis.");
        }
        if (feature.getDescription() == null || feature.getDescription().trim().isEmpty()) {
            errors.put("description", "La description est requise.");
        }
        return errors;
    }

    private Map<String, String> validatePublicite(Publicite publicite) {
        Map<String, String> errors = new HashMap<>();
        if (publicite.getTitre() == null || publicite.getTitre().trim().isEmpty()) {
            errors.put("titre", "Le titre est requis.");
        }
        if (publicite.getSousTitre() == null || publicite.getSousTitre().trim().isEmpty()) {
            errors.put("sousTitre", "Le sous-titre est requis.");
        }
        return errors;
    }

    private Map<String, String> validateBanniere(Banniere banniere) {
        Map<String, String> errors = new HashMap<>();
        if (banniere.getTitre() == null || banniere.getTitre().trim().isEmpty()) {
            errors.put("titre", "Le titre est requis.");
        }
        if (banniere.getDescription() == null || banniere.getDescription().trim().isEmpty()) {
            errors.put("description", "La description est requise.");
        }
        if (banniere.getBtnTexte() == null || banniere.getBtnTexte().trim().isEmpty()) {
            errors.put("btnTexte", "Le texte du bouton est requis.");
        }
        return errors;
    }

    private Map<String, String> validateFaq(Faq faq) {
        Map<String, String> errors = new HashMap<>();
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty()) {
            errors.put("question", "La question est requise.");
        }
        if (faq.getReponse() == null || faq.getReponse().trim().isEmpty()) {
            errors.put("reponse", "La réponse est requise.");
        }
        if (faq.getOrdre() == null) {
            faq.setOrdre(0);
        }
        if (faq.getActive() == null) {
            faq.setActive(true);
        }
        return errors;
    }

    private Map<String, String> validateTemoignage(Temoignage temoignage) {
        Map<String, String> errors = new HashMap<>();
        if (temoignage.getNom() == null || temoignage.getNom().trim().isEmpty()) {
            errors.put("nom", "Le nom est requis.");
        }
        if (temoignage.getMessage() == null || temoignage.getMessage().trim().isEmpty()) {
            errors.put("message", "Le message est requis.");
        }
        if (temoignage.getNote() == null || temoignage.getNote() < 1 || temoignage.getNote() > 5) {
            temoignage.setNote(5);
        }
        if (temoignage.getIsActif() == null) {
            temoignage.setIsActif(true);
        }
        return errors;
    }
}
