package itu.GreenField.controller;

import itu.GreenField.model.Client;
import itu.GreenField.repository.ClientRepository;
import itu.GreenField.service.ValidationMailService;
import itu.GreenField.service.ValidationService;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthClientController {

    private ClientRepository clientRepository;
    private ValidationService validationService;
    private ValidationMailService validationMailService;

    public AuthClientController(ClientRepository clientRepository, ValidationService validationService,
            ValidationMailService validationMailService) {
        this.clientRepository = clientRepository;
        this.validationService = validationService;
        this.validationMailService = validationMailService;
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
    public String verifierMail() {
        return "front/auth/email";
    }

    @PostMapping("/validation/email")
    public String verifierCode(@RequestParam String code,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        if (!validationMailService.verifier(code, email)) {
            redirectAttributes.addFlashAttribute("error", "Code de validation incorrect.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Félicitations! Votre compte est maintenant valide.");
        }

        redirectAttributes.addFlashAttribute("email", email);

        return "redirect:/validation/email";
    }

    @PostMapping("/login")
    public String traiterLogin(@RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Client client = clientRepository.findByMail(email);

        if (client == null || !client.getMotdepasse().equals(motDePasse)) {
            redirectAttributes.addFlashAttribute("error", "Email ou mot de passe incorrect.");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/login";
        }

        if (Boolean.FALSE.equals(client.getEstVerifie())) {
            redirectAttributes.addFlashAttribute("error", "Veuillez d'abord valider votre adresse email.");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/login";
        }

        session.setAttribute("client", client);

        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }

        return "redirect:/produits";
    }

    @PostMapping("/signup")
    public String traiterSignup(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String adresse,
            @RequestParam String contact,
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
        return "redirect:/produits";
    }
}