package itu.GreenField.controller;

import itu.GreenField.service.ProduitService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import itu.GreenField.model.ModeReception;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.model.TypeCommande;
import itu.GreenField.model.Commandes;
import itu.GreenField.service.CommandesService;
import itu.GreenField.service.ClientService;
import itu.GreenField.dto.CommandeBackFormDto;
import itu.GreenField.dto.CommandeBackFilterDto;
import itu.GreenField.dto.DetailCommandeBackDto;
import itu.GreenField.filtre.CalculOption;
import itu.GreenField.filtre.FiltreDateBackCommandeOption;
import itu.GreenField.filtre.FiltreNombreBackCommandeOption;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/delete/{id}")
    public ModelAndView deleteCommande(@PathVariable("id") Integer id,
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {
        // verification du mode passe
        Commandes cmd = commandeService.findById(id);
        ModelAndView mv = new ModelAndView("back/commande/listeCommande");

        try {
            commandeService.delete(cmd);
            generateListCommandesModel(mv, null, filter);
            mv.addObject("succes", "La commande #" + id + " a été supprimée avec succès.");
            return mv;
        } catch (Exception e) {
            generateListCommandesModel(mv, null, filter);
            mv.addObject("alert", "Une erreur est suvenue lors de la suppression de la commande #" + id);
            return mv;
        }
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detailCommande(@PathVariable("id") Integer id,
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {
        ModelAndView mv = new ModelAndView("back/commande/detailCommande");

        mv.addObject("commande", commandeService.findById(id));

        // verification du mode passe
        return mv;
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("commandeBackFormDto") CommandeBackFormDto form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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
            Commandes cmd = commandeService.saveBackCommande(form);
            redirectAttributes.addFlashAttribute("succes", "La commande a été sauvegardée avec succès. #" + cmd.getId());
            return "redirect:/commandes/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "back/commande/commandeCreate";
        }
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listCommandes() {
        ModelAndView mv = new ModelAndView("back/commande/listeCommande");
        CommandeBackFilterDto filter = new CommandeBackFilterDto();
        generateListCommandesModel(mv, null, filter);
        return mv;
    }

    @PostMapping({ "/", "", "/list" })
    public ModelAndView filteredlistCommande(
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {

        ModelAndView mv = new ModelAndView("back/commande/listeCommande");

        generateListCommandesModel(mv, null, filter);

        return mv;
    }

    private void generateListCommandesModel(ModelAndView mv, Model model, CommandeBackFilterDto filter) {
        Page<Commandes> commandePage = commandeService.findWithDynamicFilters(filter);

        if (mv != null) {
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
        } else if (model != null) {
            model.addAttribute("commandes", commandePage.getContent());
            model.addAttribute("totalPages", commandePage.getTotalPages());
            model.addAttribute("hasPrevious", commandePage.hasPrevious());
            model.addAttribute("hasNext", commandePage.hasNext());

            model.addAttribute("commandeFilterDto", filter);

            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("typeCommandeOptions", TypeCommande.getAllTypeCommande());
            model.addAttribute("calculOptions", CalculOption.values());
            model.addAttribute("filtreDateOptions", FiltreDateBackCommandeOption.values());
            model.addAttribute("filtreNombreOptions", FiltreNombreBackCommandeOption.values());
            model.addAttribute("statutCommandeOption", StatutCommande.getAllStatutCommande());
        }
    }
}
