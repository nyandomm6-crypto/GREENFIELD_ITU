package itu.greenfield.controller;

import itu.greenfield.service.EnvoiEmail;
import itu.greenfield.service.UtilsService;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {

    private final EnvoiEmail envoiEmail;
    private final UtilsService utilsService;

    public EmailController(EnvoiEmail envoiEmail, UtilsService utilsService) {
        this.envoiEmail = envoiEmail;
        this.utilsService = utilsService;
    }

    @GetMapping("/email")
    public String afficherFormulaire() {
        return "email";
    }

    @PostMapping("/email")
    public String envoyerEmail(
            @RequestParam String nomEntreprise,
            @RequestParam String sujet,
            @RequestParam String contenu,
            @RequestParam(required = false) MultipartFile pieceJointe,
            RedirectAttributes redirectAttributes) {

        File fichierJoint = null;

        try {
            fichierJoint = utilsService.sauvegarderPieceJointe(pieceJointe);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Erreur fichier : " + e.getMessage());
            return "redirect:/email";
        }

        envoiEmail.envoyerEmailAsync(
                nomEntreprise,
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                "s8niklaus@gmail.com",
                sujet,
                contenu,
                fichierJoint,
                true);

        redirectAttributes.addFlashAttribute(
                "success",
                "Email en cours d'envoi");

        return "redirect:/email";
    }
}