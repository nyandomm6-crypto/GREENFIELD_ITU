package itu.GreenField.controller;

import itu.GreenField.service.EnvoiEmail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {
    private final EnvoiEmail envoiEmail;

    public EmailController(EnvoiEmail envoiEmail) {
        this.envoiEmail = envoiEmail;
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

        String cheminFichier = null;

        try {
            cheminFichier = sauvegarderPieceJointe(pieceJointe);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Impossible de lire le fichier joint : " + e.getMessage());
            return "redirect:/email";
        }

        envoiEmail.envoyerEmailAsync(
                nomEntreprise,
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                "s8niklaus@gmail.com",
                sujet,
                contenu,
                cheminFichier,
                true);

        redirectAttributes.addFlashAttribute(
                "success",
                "Email en cours d'envoi. Vous pouvez continuer a utiliser l'application.");

        return "redirect:/email";
    }

    private String sauvegarderPieceJointe(MultipartFile pieceJointe) throws IOException {
        if (pieceJointe == null || pieceJointe.isEmpty()) {
            return null;
        }

        String nomOriginal = pieceJointe.getOriginalFilename();
        String nomFichier = nomOriginal == null ? "piece-jointe" : nomOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path fichierTemporaire = Files.createTempFile("greenfield-email-", "-" + nomFichier);

        Files.copy(pieceJointe.getInputStream(), fichierTemporaire, StandardCopyOption.REPLACE_EXISTING);
        return fichierTemporaire.toString();
    }

}
