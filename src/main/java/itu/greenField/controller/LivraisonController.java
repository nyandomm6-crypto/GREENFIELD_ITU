package itu.greenField.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import itu.greenField.service.CommandesService;
import itu.greenField.service.EmployesService;
import itu.greenField.service.LivraisonService;
import itu.greenField.service.VehiculeService;

@Controller
public class LivraisonController {
    private LivraisonService livraisonService;
    private VehiculeService vehiculeService;
    private EmployesService employesService;
    private CommandesService commandesService;

    public LivraisonController(LivraisonService livraisonService, VehiculeService vehiculeService,
            EmployesService employesService, CommandesService commandesService) {
        this.livraisonService = livraisonService;
        this.vehiculeService = vehiculeService;
        this.employesService = employesService;
        this.commandesService = commandesService;
    }

    @GetMapping("/livraison")
    public String CreateLivraison(Model model) {
        model.addAttribute("commandes", commandesService.getCommandesDispo());
        model.addAttribute("vehicules", vehiculeService.getVehicule());
        model.addAttribute("employes", employesService.getLivreur());

        return "back/livraison/livraisonCreate";
    }

    @PostMapping("/livraison")
    public String saveLivraison(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam Integer idVehicule,
            @RequestParam Integer idEmploye,
            @RequestParam List<Integer> idCommandes) {

        if (date != null) {
            System.out.println("Date : " + date);
            System.out.println("Vehicule : " + idVehicule);
            System.out.println("Employe : " + idEmploye);
            System.out.println("Commandes : " + idCommandes);
        } else {
            System.out.println("Vehicule : " + idVehicule);
            System.out.println("Employe : " + idEmploye);
            System.out.println("Commandes : " + idCommandes);
        }
        livraisonService.createLivraison(idVehicule, idEmploye, idCommandes, date);

        return "redirect:/livraison";
    }
}
