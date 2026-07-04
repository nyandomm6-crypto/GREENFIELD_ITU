package itu.greenField.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import itu.greenField.model.Livraison;
import itu.greenField.model.StatutLivraison;
import itu.greenField.service.CommandeService;
import itu.greenField.service.EmployesService;
import itu.greenField.service.LivraisonService;
import itu.greenField.service.VehiculeService;

@Controller
public class LivraisonController {
    private LivraisonService livraisonService;
    private VehiculeService vehiculeService;
    private EmployesService employesService;
    private CommandeService commandesService;

    public LivraisonController(LivraisonService livraisonService, VehiculeService vehiculeService,
            EmployesService employesService, CommandeService commandesService) {
        this.livraisonService = livraisonService;
        this.vehiculeService = vehiculeService;
        this.employesService = employesService;
        this.commandesService = commandesService;
    }

    @GetMapping("/livraison/create")
    public String CreateLivraison(Model model) {
        model.addAttribute("commandes", commandesService.getCommandesDispo());
        model.addAttribute("vehicules", vehiculeService.getVehicule());
        model.addAttribute("employes", employesService.getLivreur());

        return "back/livraison/livraisonCreate";
    }

    @PostMapping("/livraison/create")
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

    @GetMapping("/livraison")
    public String listLivraisons(Model model) {
        model.addAttribute("livraisons", livraisonService.getLivraisons());
        model.addAttribute("status", livraisonService.getStatutLivraisons());
        model.addAttribute("vehicules", vehiculeService.getVehicule());
        model.addAttribute("livreurs", employesService.getLivreur());

        return "back/livraison/livraisonList";
    }

    @GetMapping("/livraisons/filter")
    public String filter(
            @RequestParam(required = false) StatutLivraison statut,
            @RequestParam(required = false) Integer idVehicule,
            @RequestParam(required = false) Integer idLivreur,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            Model model) {

        List<Livraison> livraisons = livraisonService.filtrer(
                statut,
                idVehicule,
                idLivreur,
                dateDebut,
                dateFin);

        model.addAttribute("livraisons", livraisons);

        return "back/livraison/fragments/livraison-list :: liste";
    }

    @GetMapping("/livraison/{id}")
    public String showLivraisonDetails(@PathVariable Integer id, Model model) {
        Livraison livraison = livraisonService.getLivraisonById(id);
        model.addAttribute("livraison", livraison);
        return "back/livraisonDetails/details";
    }

    @PostMapping("/livraison-fille/annuler/{id}")
    @ResponseBody
    public boolean annulerLivraisonFille(@PathVariable Integer id) {
        // livraisonService.annulerLivraisonFille(id
        return true;
    }

    @PostMapping("/livraison-fille/valider/{id}")
    @ResponseBody
    public boolean valider(@PathVariable Integer id) {
        // livraisonService.validerLivraisonFille(id
        return true;
    }

    @PostMapping("/livraison-fille/reporter")
    public String reporterLivraisonFille(
            @RequestParam Integer id,
            @RequestParam Integer idLivraison,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateReport) {
        return "redirect:/livraison/" + idLivraison;
    }
}
