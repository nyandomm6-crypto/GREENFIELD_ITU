package itu.GreenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import itu.GreenField.service.LivraisonService;

@Controller
public class LivraisonController {
    private LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @GetMapping("/livraison")
    public String afficherLivraison() {
        return "front/livraison/livraisonCreate";
    }
}
