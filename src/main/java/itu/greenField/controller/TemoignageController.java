package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import itu.greenField.model.Client;
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
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "5") Integer note,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/login?redirect=/";
        }

        Temoignage temoignage = new Temoignage();
        String nomClient = (client.getNom() != null && !client.getNom().trim().isEmpty())
                ? client.getNom().trim()
                : "Client";

        temoignage.setNom(nomClient);
        temoignage.setPoste("client");
        temoignage.setMessage(message);
        temoignage.setClient(client);
        temoignage.setNote(note == null ? 5 : Math.max(1, Math.min(5, note)));
        temoignage.setIsActif(true);

        temoignageService.enregistrer(temoignage);
        redirectAttributes.addFlashAttribute("success", "Votre témoignage a bien été enregistré.");
        return "redirect:/";
    }
}
