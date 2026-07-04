package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/emp")
public class AuthEmployeController {
    @GetMapping("/login")
    public String accueil(HttpSession session, Model model) {

        return "back/auth/login";
    }
}
