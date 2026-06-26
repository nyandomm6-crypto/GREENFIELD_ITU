package itu.GreenField.controller;

import itu.GreenField.service.ProduitService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import itu.GreenField.service.CommandesService;
import itu.GreenField.service.ClientService;
import itu.GreenField.dto.CommandeBackFormDto;
import itu.GreenField.dto.DetailCommandeBackDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

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
        return mv;
    }

    @GetMapping("/form/edit")
    public ModelAndView showEditForm(@RequestParam("id") Integer id) {
        ModelAndView mv = new ModelAndView("commandeEdit");
        mv.addObject("commande", commandeService.getCommandeById(id));
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
        ModelAndView mv = new ModelAndView("list-commandes");
        mv.addObject("commandes", commandeService.getCommandesDispo());
        return mv;
    }
}
