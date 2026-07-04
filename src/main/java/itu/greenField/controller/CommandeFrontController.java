package itu.greenField.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Client;
import itu.greenField.model.ModeReception;
import itu.greenField.model.Panier;
import itu.greenField.repository.PointDeVenteRepository;
import itu.greenField.service.CommandeFrontService;
import itu.greenField.service.PanierService;

@Controller
public class CommandeFrontController {

    private final CommandeFrontService commandeService;
    private final PanierService panierService;
    private final PointDeVenteRepository pointDeVenteRepository;

    public CommandeFrontController(CommandeFrontService commandeService, PanierService panierService,
            PointDeVenteRepository pointDeVenteRepository) {
        this.commandeService = commandeService;
        this.panierService = panierService;
        this.pointDeVenteRepository = pointDeVenteRepository;
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
        model.addAttribute("pointsDeVente", pointDeVenteRepository.findAll());
        return "front/commande/recapitulatif";
    }

    /**
     * Valide définitivement l'achat. Une connexion est obligatoire : sans
     * client en session, l'utilisateur est renvoyé vers la page de login.
     */
    @PostMapping("/commande/valider")
    public String validerCommande(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            RedirectAttributes redirectAttributes,

            @RequestParam("modeReception") String mode,
            @RequestParam(value = "adresse", required = false) String adresse,
            @RequestParam(value = "pointRetrait", required = false) String pointRetrait,
            @RequestParam(value = "dateReception") String date,
            @RequestParam(value = "heureReception", required = false) String heure) {

        Client client = (Client) session.getAttribute(PanierController.CLE_SESSION_CLIENT);

        if (client == null) {
            return "redirect:/login?redirect=/commande/recapitulatif";
        }

        Panier panier = PanierController.resoudrePanierCourant(
                request, response, session, panierService);

        if (panierService.listerLignes(panier).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le panier est vide.");
            return "redirect:/panier";
        }

        LocalDateTime dateHeure = LocalDateTime.now();
        if (date != null && heure != null && !date.isBlank() && !heure.isBlank()) {
            try {
                dateHeure = LocalDateTime.parse(date.trim() + "T" + heure.trim());
            } catch (DateTimeParseException exception) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "La date ou l'heure de réception est invalide.");
                return "redirect:/commande/recapitulatif";
            }
        }
        ModeReception modeReception = ModeReception.valueOf(mode);
        String erreur = commandeService.validerAchat(
                panier,
                client,
                modeReception,
                adresse,
                pointRetrait,
                dateHeure);

        if (erreur != null) {
            redirectAttributes.addFlashAttribute("error", erreur);
            return "redirect:/panier";
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Votre achat a été validé avec succès !");

        return "redirect:/produits";
    }
}
