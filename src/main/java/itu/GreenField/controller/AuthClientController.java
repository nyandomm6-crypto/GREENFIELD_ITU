package itu.GreenField.controller;

import itu.GreenField.model.Client;
import itu.GreenField.repository.ClientRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthClientController {
    private ClientRepository client;

    public AuthClientController(ClientRepository client) {
        this.client = client;
    }

    @GetMapping("/login")
    public String afficherLogin() {
        return "front/auth/login";
    }

    @GetMapping("/signup")
    public String afficherSignup() {
        return "front/auth/signup";
    }

    @PostMapping("/login")
    public String traiterLogin(@RequestParam String email, @RequestParam String motDePasse) {

        return "front/auth/dashboard";
    }

    @PostMapping("/signup")
    public String traiterSignup(@RequestParam String email, @RequestParam String motDePasse) {
        return "front/auth/signup";
    }

}
