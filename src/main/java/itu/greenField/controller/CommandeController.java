package itu.greenField.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Client;
import itu.greenField.model.Panier;
import itu.greenField.service.CommandeService;
import itu.greenField.service.PanierService;

@Controller
public class CommandeController {

    private final CommandeService commandeService;
    private final PanierService panierService;

    public CommandeController(CommandeService commandeService, PanierService panierService) {
        this.commandeService = commandeService;
        this.panierService = panierService;
    }

    /**
     * Affiche le récapitulatif avant validation. Si l'utilisateur n'est pas
     * connecté, on le redirige vers la page de connexion (qui le renverra
     * ici une fois connecté).
     */
    @GetMapping("/commande/recapitulatif")
    public String afficherRecapitulatif(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Model model) {

        Client client = (Client) session.getAttribute(PanierController.CLE_SESSION_CLIENT);
        if (client == null) {
            return "redirect:/login?redirect=/commande/recapitulatif";
        }

        Panier panier = PanierController.resoudrePanierCourant(request, response, session, panierService);
        if (panierService.listerLignes(panier).isEmpty()) {
            return "redirect:/panier";
        }

        model.addAttribute("panier", panier);
        model.addAttribute("lignes", panierService.listerLignes(panier));
        model.addAttribute("total", panierService.calculerTotal(panier));
        model.addAttribute("client", client);
        return "front/commande/recapitulatif";
    }

    /**
     * Valide définitivement l'achat. Une connexion est obligatoire : sans
     * client en session, l'utilisateur est renvoyé vers la page de login.
     */
    @PostMapping("/commande/valider")
    public String validerCommande(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Client client = (Client) session.getAttribute(PanierController.CLE_SESSION_CLIENT);
        if (client == null) {
            return "redirect:/login?redirect=/commande/recapitulatif";
        }

        Panier panier = PanierController.resoudrePanierCourant(request, response, session, panierService);
        if (panierService.listerLignes(panier).isEmpty()) {
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
