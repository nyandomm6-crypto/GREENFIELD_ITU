package itu.greenfield.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatsPageController {

    @GetMapping({"/dashboard", "/stats"})
    public String dashboard() {
        return "front/stats/dashboard";
    }

    @GetMapping("/stats/ventes")
    public String ventes() {
        return "front/stats/ventes";
    }

    @GetMapping("/stats/produits")
    public String produits() {
        return "front/stats/produits";
    }

    @GetMapping("/stats/clients")
    public String clients() {
        return "front/stats/clients";
    }

    @GetMapping("/stats/tresorerie")
    public String tresorerie() {
        return "front/stats/tresorerie";
    }
}