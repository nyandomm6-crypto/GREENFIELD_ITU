package itu.greenfield.controller;

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

import java.util.List;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/commandes")
public class CommandeController {
    private final ProduitService produitService;
    private final CommandesService commandeService;
    private final ClientService clientService;
    private final StatutCommandeService statutCommandeService;
    private final ProvinceLivraisonService provinceLivraisonService;

    public CommandeController(CommandesService commandeService, ClientService clientService,
            ProduitService produitService, StatutCommandeService statutCommandeService,
            ProvinceLivraisonService provinceLivraisonService) {
        this.commandeService = commandeService;
        this.clientService = clientService;
        this.produitService = produitService;
        this.statutCommandeService = statutCommandeService;
        this.provinceLivraisonService = provinceLivraisonService;
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
        mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        return mv;
    }

    @GetMapping("/fo/form/new")
    public ModelAndView showCreateFrontForm() {
        ModelAndView mv = new ModelAndView("front/commande/commandeCreate");
        // mv.addObject("clients", clientService.getAll());
        // mv.addObject("globalError", "Test error");
        CommandeBackFormDto dto = new CommandeBackFormDto();
        dto.getDetailsCommande().add(new DetailCommandeBackDto());

        mv.addObject("commandeBackFormDto", dto);
        mv.addObject("produits", produitService.getAllProduits());
        mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
        mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        return mv;
    }

    @GetMapping("/form/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("back/commande/commandeCreate");
        Commandes cmd = commandeService.findById(id);

        try {
            commandeService.checkIfUpdatable(cmd);

            CommandeBackFormDto dto = new CommandeBackFormDto(cmd);

            mv.addObject("commandeBackFormDto", dto);
            mv.addObject("produits", produitService.getAllProduits());
            mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        } catch (Exception e) {
            mv.addObject("alert", "La commande #" + id + " ne peut pas être modifiée. " + e.getMessage());
            mv.setViewName("back/commande/detailCommande");
            mv.addObject("commande", cmd);
        }

        return mv;
    }

    @GetMapping("/fo/form/edit/{id}")
    public ModelAndView showEditFrontForm(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("front/commande/commandeCreate");
        Commandes cmd = commandeService.findById(id);

        try {
            commandeService.checkIfUpdatable(cmd);

            CommandeBackFormDto dto = new CommandeBackFormDto(cmd);

            mv.addObject("commandeBackFormDto", dto);
            mv.addObject("produits", produitService.getAllProduits());
            mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        } catch (Exception e) {
            mv.addObject("alert", "La commande #" + id + " ne peut pas être modifiée. " + e.getMessage());
            mv.setViewName("front/commande/detailCommande");
            mv.addObject("commande", cmd);
        }

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
            generateListCommandesModel(mv, filter);
            mv.addObject("succes", "La commande #" + id + " a été supprimée avec succès.");
            return mv;
        } catch (Exception e) {
            generateListCommandesModel(mv, filter);
            mv.addObject("alert", "Une erreur est suvenue lors de la suppression de la commande #" + id);
            e.printStackTrace();
            return mv;
        }
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detailCommande(@PathVariable("id") Integer id,
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {
        ModelAndView mv = new ModelAndView("back/commande/detailCommande");
        Commandes cmd = commandeService.findById(id);
        mv.addObject("commande", cmd);

        // verification du mode passe
        return mv;
    }

    @GetMapping("/fo/detail/{id}")
    public ModelAndView detailFrontCommande(@PathVariable("id") Integer id,
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {
        ModelAndView mv = new ModelAndView("front/commande/detailCommande");
        Commandes cmd = commandeService.findById(id);
        if(cmd.getClient() != null && cmd.getClient().getId() != 3) {
            mv.setViewName("error");
            return mv;
        }
        mv.addObject("commande", cmd);

        // verification du mode passe
        return mv;
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("commandeBackFormDto") CommandeBackFormDto form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if ("Livraison_Domicile".equalsIgnoreCase(form.getModeReception())) {
            if (form.getAddress() == null || form.getAddress().trim().isEmpty()) {
                bindingResult.rejectValue("address", "error.address",
                        "L'adresse est obligatoire pour une livraison à domicile.");
            }
            if (form.getProvinceId() == null) {
                bindingResult.rejectValue("provinceId", "error.provinceId",
                        "La province de livraison est obligatoire pour une livraison à domicile.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            return "back/commande/commandeCreate";
        }

        try {
            Commandes cmd = commandeService.saveBackCommande(form);
            redirectAttributes.addFlashAttribute("succes",
                    "La commande a été sauvegardée avec succès. #" + cmd.getId());
            return "redirect:/commandes/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "back/commande/commandeCreate";
        }
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listCommandes() {
        ModelAndView mv = new ModelAndView("back/commande/listeCommande");
        CommandeBackFilterDto filter = new CommandeBackFilterDto();
        generateListCommandesModel(mv, filter);
        return mv;
    }

    @PostMapping({ "/", "", "/list" })
    public ModelAndView filteredlistCommande(
            @ModelAttribute("commandeFilterDto") CommandeBackFilterDto filter) {

        ModelAndView mv = new ModelAndView("back/commande/listeCommande");

        generateListCommandesModel(mv, filter);

        return mv;
    }

    @GetMapping({ "/fo/", "/fo", "/fo/list" })
    public ModelAndView listCommandesFront() {
        ModelAndView mv = new ModelAndView("front/commande/listeCommande");
        CommandeBackFilterDto filter = new CommandeBackFilterDto();
        // On prend le client connecté pour filtrer les commandes
        filter.setClientId(List.of(3));
        generateListCommandesModel(mv, filter);
        return mv;
    }

    private void generateListCommandesModel(ModelAndView mv, CommandeBackFilterDto filter) {
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
            mv.addObject("statutCommandeOptions", statutCommandeService.getAll());
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        }
    }

    private void generateListCommandesModel(Model model, CommandeBackFilterDto filter) {
        Page<Commandes> commandePage = commandeService.findWithDynamicFilters(filter);

        if (model != null) {
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
            model.addAttribute("statutCommandeOptions", statutCommandeService.getAll());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        }
    }
}
