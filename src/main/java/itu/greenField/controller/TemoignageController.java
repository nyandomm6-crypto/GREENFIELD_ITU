package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Temoignage;
import itu.greenField.service.TemoignageService;

@Controller
public class TemoignageController {

    private final TemoignageService temoignageService;

    public TemoignageController(TemoignageService temoignageService) {
        this.temoignageService = temoignageService;
    }

    @PostMapping("/temoignages")
    public String enregistrer(
            @RequestParam String nom,
            @RequestParam String poste,
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "false") Boolean isActif,
            RedirectAttributes redirectAttributes) {
        Temoignage temoignage = new Temoignage();
        temoignage.setNom(nom);
        temoignage.setPoste(poste);
        temoignage.setMessage(message);
        temoignage.setIsActif(isActif);

        temoignageService.enregistrer(temoignage);
        redirectAttributes.addFlashAttribute("success", "Votre témoignage a bien été enregistré.");
        return "redirect:/";
    }
}
