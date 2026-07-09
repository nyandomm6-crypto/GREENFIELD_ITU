package itu.greenField.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.dto.CommandeBackFormDto;
import itu.greenField.dto.DetailCommandeBackDto;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.ModeReception;
import itu.greenField.service.CommandesService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.EmployesService;
import itu.greenField.service.PaiementService;
import itu.greenField.service.PointDeVenteService;
import itu.greenField.service.ProvinceLivraisonService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/caissier")
@RequiredArgsConstructor
public class CaissierController {

    private final EmployesService employesService;
    private final CommandesService commandesService;
    private final ProduitService produitService;
    private final PointDeVenteService pointDeVenteService;
    private final ProvinceLivraisonService provinceLivraisonService;
    private final PaiementService paiementService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("employe", employe);
        model.addAttribute("commandes", commandesService.getCommandesDispo());
        model.addAttribute("paiements", paiementService.findByStatut(null));
        return "back/caissier/dashboard";
    }

    @GetMapping("/produits")
    public String produits(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("employe", employe);
        model.addAttribute("produits", produitService.getAllProduits());
        return "back/caissier/produits";
    }

    @GetMapping("/commandes")
    public String commandes(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("employe", employe);
        model.addAttribute("commandes", commandesService.getCommandesDispo());
        return "back/caissier/commandes";
    }

    @GetMapping("/paiements")
    public String paiements(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("employe", employe);
        model.addAttribute("paiements", paiementService.findByStatut(null));
        return "back/caissier/paiements";
    }

    @GetMapping("/commande/new")
    public String nouvelleCommande(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        CommandeBackFormDto dto = new CommandeBackFormDto();
        dto.getDetailsCommande().add(new DetailCommandeBackDto());
        model.addAttribute("commandeBackFormDto", dto);
        model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
        return "back/caissier/commandeCreate";
    }

    @PostMapping("/commande/save")
    public String saveCommande(@Valid @ModelAttribute("commandeBackFormDto") CommandeBackFormDto form,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Employes employe = (Employes) session.getAttribute("employe");
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        if ("Livraison_Domicile".equalsIgnoreCase(form.getModeReception())) {
            if (form.getAddress() == null || form.getAddress().trim().isEmpty()) {
                bindingResult.rejectValue("address", "error.address",
                        "L'adresse est obligatoire pour une livraison à domicile.");
            }
            if (form.getProvinceId() == null) {
                bindingResult.rejectValue("provinceId", "error.provinceId",
                        "La province de livraison est obligatoire.");
            }
        }
        if ("Retrait_Boutique".equalsIgnoreCase(form.getModeReception()) && form.getPointDeVenteId() == null) {
            bindingResult.rejectValue("pointDeVenteId", "error.pointDeVenteId",
                    "Le point de vente de retrait est obligatoire.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
            return "back/caissier/commandeCreate";
        }

        try {
            commandesService.saveBackCommande(form);
            redirectAttributes.addFlashAttribute("succes", "Commande enregistrée avec succès.");
            return "redirect:/caissier/commandes";
        } catch (Exception e) {
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "back/caissier/commandeCreate";
        }
    }

}
