package itu.greenfield.controller;

import itu.greenfield.model.Client;
import itu.greenfield.repository.ClientRepository;
import itu.greenfield.service.ValidationMailService;
import itu.greenfield.service.ValidationService;

import java.util.Map;

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
    public String afficherLogin() {
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
            RedirectAttributes redirectAttributes) {
        Client cli = clientRepository.findByMail(email);
        if (cli == null) {
            redirectAttributes.addFlashAttribute("error", "compte tsy misy");
            return "redirect:/login";
        }
        if (cli.getMotdepasse().equals(motDePasse)) {
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "mot de passe diso");
            return "redirect:/login";
        }
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
}