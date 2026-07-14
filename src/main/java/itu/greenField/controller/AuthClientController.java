package itu.greenField.controller;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.Employes;
import itu.greenField.repository.ClientRepository;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.StatutCommandeRepository;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.CommandeFrontService;
import itu.greenField.service.EmployesService;
import itu.greenField.service.PanierService;
import itu.greenField.service.ValidationMailService;
import itu.greenField.service.ValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthClientController {

    private ClientRepository clientRepository;
    private ValidationService validationService;
    private ValidationMailService validationMailService;
    private PanierService panierService;
    private CommandeFrontService commandeService;
    private CommandesRepository commandesRepository;
    private StatutCommandeRepository statutCommandeRepository;
    private EmployesService employesService;

    public AuthClientController(ClientRepository clientRepository, ValidationService validationService,
            ValidationMailService validationMailService, PanierService panierService,
            CommandeFrontService commandeService, CommandesRepository commandesRepository,
            StatutCommandeRepository statutCommandeRepository, EmployesService employesService) {
        this.clientRepository = clientRepository;
        this.validationService = validationService;
        this.validationMailService = validationMailService;
        this.panierService = panierService;
        this.commandeService = commandeService;
        this.commandesRepository = commandesRepository;
        this.statutCommandeRepository = statutCommandeRepository;
        this.employesService = employesService;
    }

    @GetMapping("/login")
    public String afficherLogin(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "front/auth/login";
    }

    @GetMapping("/signup")
    public String afficherSignup() {
        return "front/auth/signup";
    }

    @GetMapping("/validation/email")
    public String verifierMail(HttpSession session, Model model) {
        Object sessionEmail = session.getAttribute("validationEmail");
        if (sessionEmail != null) {
            model.addAttribute("email", sessionEmail.toString());
        }
        return "front/auth/email";
    }

    @PostMapping("/validation/email")
    public String verifierCode(@RequestParam String code,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!validationMailService.verifier(code, email)) {
            redirectAttributes.addFlashAttribute("error", "Code de validation incorrect.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Félicitations! Votre compte est maintenant valide.");
        }

        session.setAttribute("validationEmail", email);
        redirectAttributes.addFlashAttribute("email", email);

        return "redirect:/validation/email";
    }

    /**
     * Login unifié : un seul formulaire pour tout le monde.
     * On cherche l'email d'abord parmi les employés (back-office), puis parmi
     * les clients (boutique). La redirection dépend ensuite du type de compte
     * et, pour les employés, de leur rôle.
     */
    @PostMapping("/login")
    public String traiterLogin(@RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1) Tentative EMPLOYÉ (admin / caissier / livreur…)
        Employes employe = employesService.findByEmail(email);
        if (employe != null && employe.getMotdepasse().equals(motDePasse)) {
            session.setAttribute(AuthGuard.SESSION_KEY, employesService.getById(employe.getId()));
            return redirectSelonRole(employe, redirect);
        }

        // 2) Tentative CLIENT (boutique)
        Client client = clientRepository.findByMail(email);
        if (client == null || !client.getMotdepasse().equals(motDePasse)) {
            redirectAttributes.addFlashAttribute("error", "Email ou mot de passe incorrect.");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/login";
        }

        if (Boolean.FALSE.equals(client.getEstVerifie())) {
            session.setAttribute("validationEmail", client.getMail());
            redirectAttributes.addFlashAttribute("error", "Veuillez d'abord valider votre adresse email.");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/validation/email";
        }

        session.setAttribute("client", client);
        rattacherPanierAnonyme(request, response, client);

        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }

        return "redirect:/commandes";
    }

    /** Destination back-office d'un employé selon son rôle. */
    private String redirectSelonRole(Employes employe, String redirect) {
        switch (employe.getRole()) {
            case Livreur:
                return "redirect:/livreurs/dashboard";
            case Caissier:
                if (redirect != null && !redirect.isBlank() && redirect.startsWith("/caissier")) {
                    return "redirect:" + redirect;
                }
                return "redirect:/caissier/dashboard";
            case Administrateur:
                return "redirect:/commandes/list";
            default:
                // Rôles sans interface dédiée : retour au login.
                return "redirect:/login";
        }
    }

    /**
     * Si l'utilisateur avait un panier anonyme (cookie), on le rattache à
     * son compte désormais connecté, puis on supprime le cookie devenu
     * inutile.
     */
    private void rattacherPanierAnonyme(HttpServletRequest request, HttpServletResponse response, Client client) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (PanierController.COOKIE_PANIER.equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        panierService.rattacherAuClient(token, client);

        if (token != null) {
            Cookie cookieVide = new Cookie(PanierController.COOKIE_PANIER, "");
            cookieVide.setPath("/");
            cookieVide.setMaxAge(0);
            response.addCookie(cookieVide);
        }
    }

    @GetMapping("/validation/renvoyer")
    public String renvoyerCode(@RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            validationMailService.send(email);
            session.setAttribute("validationEmail", email);
            redirectAttributes.addFlashAttribute("success", "Un nouveau code a été envoyé à votre adresse email.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/validation/email";
    }

    @PostMapping("/signup")
    public String traiterSignup(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String adresse,
            @RequestParam String contact,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        boolean valide = validationService
                .validationSignup(email, motDePasse, nom, prenom, adresse, contact)
                .isEmpty();
        if (valide) {
            Client client = new Client();
            client.setMail(email);
            client.setMotdepasse(motDePasse);
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setAdresse(adresse);
            client.setContact(contact);
            clientRepository.save(client);
            validationMailService.send(email);
            redirectAttributes.addFlashAttribute("email", email);
            session.setAttribute("validationEmail", email);

            return "redirect:/validation/email";
        }

        return "front/auth/signup";
    }

    @PostMapping(value = "/validation", produces = "application/json")
    @ResponseBody
    public Map<String, String> validationSignup(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String motDePasse,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String contact) {

        return validationService.validationSignup(email, motDePasse, nom, prenom, adresse, contact);
    }

    @PostMapping("/logout")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String deconnexionc(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profil")
    public String afficherProfil(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client != null) {
            model.addAttribute("client", client);
            return "front/profil/profil";
        }
        return "redirect:/login";
    }

    @GetMapping("/profil/edit")
    public String editProfil(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client != null) {
            model.addAttribute("client", client);
            return "front/profil/editProfil";
        }
        return "redirect:/login";
    }

    @PostMapping("/profil/edit")
    public String updateProfil(@ModelAttribute Client client, HttpSession session) {
        Client clientConnecte = (Client) session.getAttribute("client");
        if (clientConnecte != null) {
            client.setId(clientConnecte.getId());
            client.setEstVerifie(true);
            clientRepository.save(client);
            session.setAttribute("client", client);
            return "redirect:/profil";
        }
        return "redirect:/login";
    }

    @GetMapping("/commandes")
    public String afficherCommandes(HttpSession session,
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client != null) {
            page = Math.max(page, 0);
            size = Math.max(size, 1);
            Page<Commandes> commandesPage = commandeService.findByClient(client, motCle, statut,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datecommande")));
            model.addAttribute("client", client);
            model.addAttribute("commandes", commandesPage.getContent());
            model.addAttribute("currentPage", commandesPage.getNumber());
            model.addAttribute("totalPages", commandesPage.getTotalPages());
            model.addAttribute("size", commandesPage.getSize());
            model.addAttribute("motCle", motCle);
            model.addAttribute("statut", statut);
            model.addAttribute("statutOptions",
                    statutCommandeRepository.findAll().stream()
                            .filter(Objects::nonNull)
                            .map(s -> s.getNom())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            return "front/commande/commandes";
        }
        return "redirect:/login";
    }

    @GetMapping("/commandes/{id}")
    public String voirCommande(@PathVariable Integer id, HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/login";
        }

        Commandes commande = commandesRepository.getById(id);
        if (commande == null || commande.getClient() == null
                || !commande.getClient().getId().equals(client.getId())) {
            return "redirect:/commandes";
        }

        List<DetailsCommande> details = commande.getDetailsCommande();
        if (details == null) {
            details = new ArrayList<>();
        } else {
            details.size();
        }

        model.addAttribute("client", client);
        model.addAttribute("commande", commande);
        model.addAttribute("detailsCommande", details);
        return "front/commande/detail";
    }
}