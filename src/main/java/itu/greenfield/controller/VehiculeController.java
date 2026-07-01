package itu.greenfield.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import itu.greenfield.model.StatutVehicule;
import itu.greenfield.model.Vehicule;
import itu.greenfield.service.VehiculeService;

@Controller
@RequestMapping("/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @Autowired
    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public String listeVehicules(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) StatutVehicule statut,
            Model model) {
        model.addAttribute("vehicules", vehiculeService.listerEtFiltrer(motCle, date, statut));
        model.addAttribute("motCle", motCle);
        model.addAttribute("date", date);
        model.addAttribute("statut", statut);
        model.addAttribute("statuts", StatutVehicule.values());
        return "front/vehicules/list";
    }

    @GetMapping("/ajouter")
    public String afficherFormulaireAjout(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("statuts", StatutVehicule.values());
        model.addAttribute("edit", false);
        return "front/vehicules/form";
    }

    @PostMapping("/ajouter")
    public String creerVehicule(@ModelAttribute Vehicule vehicule) {
        vehiculeService.ajouterVehicule(vehicule);
        return "redirect:/vehicules";
    }

    @GetMapping("/{id}")
    public String voirDetails(@PathVariable Integer id, Model model) {
        Vehicule vehicule = vehiculeService.obtenirParId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule introuvable"));
        model.addAttribute("vehicule", vehicule);
        return "front/vehicules/details";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model) {
        Vehicule vehicule = vehiculeService.obtenirParId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule introuvable"));
        model.addAttribute("vehicule", vehicule);
        model.addAttribute("statuts", StatutVehicule.values());
        model.addAttribute("edit", true);
        return "front/vehicules/form";
    }

    @PostMapping("/{id}/modifier")
    public String modifierVehicule(@PathVariable Integer id, @ModelAttribute Vehicule vehicule) {
        vehiculeService.modifierVehicule(id, vehicule);
        return "redirect:/vehicules";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerVehicule(@PathVariable Integer id) {
        vehiculeService.supprimerVehicule(id);
        return "redirect:/vehicules";
    }
}
