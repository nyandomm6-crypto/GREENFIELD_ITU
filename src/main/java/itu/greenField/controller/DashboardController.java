package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String accueil(HttpSession session) {
        // L'utilisateur est déjà dans la session si connecté
        // via votre système d'authentification
        return "front/accueil/accueil";
    }
}