package itu.GreenField.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.GreenField.model.Client;
import itu.GreenField.panier.Panier;
import itu.GreenField.service.CommandeService;

@Controller
public class CommandeController {

    private static final String CLE_SESSION_PANIER = "panier";
    private static final String CLE_SESSION_CLIENT = "client";

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Affiche le récapitulatif avant validation. Si l'utilisateur n'est pas
     * connecté, on le redirige vers la page de connexion (qui le renverra
     * ici une fois connecté).
     */
    @GetMapping("/commande/recapitulatif")
    public String afficherRecapitulatif(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute(CLE_SESSION_CLIENT);
        if (client == null) {
            return "redirect:/login?redirect=/commande/recapitulatif";
        }

        Panier panier = (Panier) session.getAttribute(CLE_SESSION_PANIER);
        if (panier == null || panier.estVide()) {
            return "redirect:/panier";
        }

        model.addAttribute("panier", panier);
        model.addAttribute("client", client);
        return "front/commande/recapitulatif";
    }

    /**
     * Valide définitivement l'achat. Une connexion est obligatoire : sans
     * client en session, l'utilisateur est renvoyé vers la page de login.
     */
    @PostMapping("/commande/valider")
    public String validerCommande(HttpSession session, RedirectAttributes redirectAttributes) {
        Client client = (Client) session.getAttribute(CLE_SESSION_CLIENT);
        if (client == null) {
            return "redirect:/login?redirect=/commande/recapitulatif";
        }

        Panier panier = (Panier) session.getAttribute(CLE_SESSION_PANIER);
        if (panier == null || panier.estVide()) {
            redirectAttributes.addFlashAttribute("error", "Le panier est vide.");
            return "redirect:/panier";
        }

        String erreur = commandeService.validerAchat(panier, client);

        if (erreur != null) {
            redirectAttributes.addFlashAttribute("error", erreur);
            return "redirect:/panier";
        }

        redirectAttributes.addFlashAttribute("success", "Votre achat a été validé avec succès !");
        return "redirect:/produits";
    }
}
