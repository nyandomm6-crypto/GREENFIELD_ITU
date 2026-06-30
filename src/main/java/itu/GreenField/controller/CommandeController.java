package itu.greenfield.controller;

import itu.greenfield.service.ProduitService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import itu.greenfield.model.ModeReception;
import itu.greenfield.model.StatutCommande;
import itu.greenfield.model.TypeCommande;
import itu.greenfield.model.Commandes;
import itu.greenfield.service.CommandesService;
import itu.greenfield.service.ClientService;
import itu.greenfield.dto.CommandeBackFormDto;
import itu.greenfield.dto.CommandeBackFilterDto;
import itu.greenfield.dto.DetailCommandeBackDto;
import itu.greenfield.filtre.CalculOption;
import itu.greenfield.filtre.FiltreDateBackCommandeOption;
import itu.greenfield.filtre.FiltreNombreBackCommandeOption;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/commandes")
public class CommandeController {
    private final ProduitService produitService;
    private final CommandesService commandeService;
    private final ClientService clientService;

    public CommandeController(CommandesService commandeService, ClientService clientService,
            ProduitService produitService) {
        this.commandeService = commandeService;
        this.clientService = clientService;
        this.produitService = produitService;
    }

    @GetMapping("/form/new")
    public ModelAndView showCreateForm() {
        ModelAndView mv = new ModelAndView("back/commande/commandeCreate");
        // mv.addObject("clients", clientService.getAll());
        // mv.addObject("globalError", "Test error");
        CommandeBackFormDto dto = new CommandeBackFormDto();
        dto.getDetailsCommande().add(new DetailCommandeBackDto());

        mv.addObject("commandeBackFormDto", dto);
        mv.addObject("produits", produitService.getAllProduits());
        mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
        return mv;
    }

    @GetMapping("/form/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("back/commande/commandeCreate");
        CommandeBackFormDto dto = new CommandeBackFormDto(commandeService.getCommandeById(id));

        mv.addObject("commandeBackFormDto", dto);
        mv.addObject("produits", produitService.getAllProduits());
        mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
        return mv;
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("commandeBackFormDto") CommandeBackFormDto form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            model.addAttribute("produits", produitService.getAllProduits());
            return "back/commande/commandeCreate";
        }

        if ("Livraison_Domicile".equalsIgnoreCase(form.getModeReception())) {
            if (form.getAddress() == null || form.getAddress().trim().isEmpty()) {
                bindingResult.rejectValue("address", "error.address",
                        "L'adresse est obligatoire pour une livraison à domicile.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            model.addAttribute("produits", produitService.getAllProduits());
            return "back/commande/commandeCreate";
        }

        try {
            commandeService.saveBackCommande(form);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "boutique/formulaire-commande";
        }
        return "redirect:/commandes/list";
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listCommandes() {
        ModelAndView mv = new ModelAndView("back/commande/listeCommande");
        CommandeBackFilterDto filter = new CommandeBackFilterDto();
        int size = filter.getLineNumber() != null ? filter.getLineNumber() : 10;
        int page = filter.getPageNumber() != null ? filter.getPageNumber() : 1;

        Page<Commandes> commandePage = commandeService.getCommandesPagine(page - 1, size);

        mv.addObject("commandes", commandePage.getContent());
        /* mv.addObject("currentPage", page);
        mv.addObject("pageSize", size); */
        mv.addObject("totalPages", commandePage.getTotalPages());
        mv.addObject("hasPrevious", commandePage.hasPrevious());
        mv.addObject("hasNext", commandePage.hasNext());

        mv.addObject("commandeFilterDto", filter);
        mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
        mv.addObject("typeCommandeOptions", TypeCommande.getAllTypeCommande());
        mv.addObject("calculOptions", CalculOption.values());
        mv.addObject("filtreDateOptions", FiltreDateBackCommandeOption.values());
        mv.addObject("filtreNombreOptions", FiltreNombreBackCommandeOption.values());
        mv.addObject("statutCommandeOption", StatutCommande.getAllStatutCommande());
        return mv;
    }

    @PostMapping({ "/", "", "/list" })
    public ModelAndView filteredlistCommande(
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {

        ModelAndView mv = new ModelAndView("back/commande/listeCommande");

        Page<Commandes> commandePage = commandeService.findWithDynamicFilters(filter);

        mv.addObject("commandes", commandePage.getContent());
        mv.addObject("totalPages", commandePage.getTotalPages());
        mv.addObject("hasPrevious", commandePage.hasPrevious());
        mv.addObject("hasNext", commandePage.hasNext());

        mv.addObject("commandeFilterDto", filter);

        mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
        mv.addObject("typeCommandeOptions", TypeCommande.getAllTypeCommande());
        mv.addObject("calculOptions", CalculOption.values());
        mv.addObject("filtreDateOptions", FiltreDateBackCommandeOption.values());
        mv.addObject("filtreNombreOptions", FiltreNombreBackCommandeOption.values());
        mv.addObject("statutCommandeOption", StatutCommande.getAllStatutCommande());

        return mv;
    }
}
