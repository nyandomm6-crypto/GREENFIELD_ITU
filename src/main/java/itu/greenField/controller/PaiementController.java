package itu.greenField.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.dto.CommandeBackFilterDto;
import itu.greenField.dto.CommandeBackFormDto;
import itu.greenField.dto.DetailCommandeBackDto;
import itu.greenField.filtre.CalculOption;
import itu.greenField.filtre.FiltreDateBackCommandeOption;
import itu.greenField.filtre.FiltreNombreBackCommandeOption;
import itu.greenField.model.Commandes;
import itu.greenField.model.ModeReception;
import itu.greenField.model.TypeCommande;
import itu.greenField.service.ClientService;
import itu.greenField.service.CommandesService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.ProvinceLivraisonService;
import itu.greenField.service.StatutCommandeService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping("paiements")
public class PaiementController {
    // private final ProduitService produitService;
    // private final CommandesService commandeService;
    // private final ClientService clientService;
    // private final StatutCommandeService statutCommandeService;
    // private final ProvinceLivraisonService provinceLivraisonService;

    // public CommandeController(CommandesService commandeService, ClientService clientService,
    //         ProduitService produitService, StatutCommandeService statutCommandeService,
    //         ProvinceLivraisonService provinceLivraisonService) {
    //     this.commandeService = commandeService;
    //     this.clientService = clientService;
    //     this.produitService = produitService;
    //     this.statutCommandeService = statutCommandeService;
    //     this.provinceLivraisonService = provinceLivraisonService;
    // }

    @GetMapping("/choix")
    public ModelAndView showCreateForm() {
        ModelAndView mv = new ModelAndView("front/paiement/choixPaiement");
        return mv;
    }


    
}
