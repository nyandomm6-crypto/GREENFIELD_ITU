package itu.GreenField.controller;

import itu.GreenField.service.ProduitService;
import org.springframework.stereotype.Controller;
import itu.GreenField.service.CommandesService;
import itu.GreenField.service.ClientService;
import itu.GreenField.dto.CommandeBackFormDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/commandes")
public class CommandeController {
    private final ProduitService produitService;
    private final CommandesService commandeService;
    private final ClientService clientService;

    public CommandeController(CommandesService commandeService, ClientService clientService, ProduitService produitService) {
        this.commandeService = commandeService;
        this.clientService = clientService;
        this.produitService = produitService;
    }

    @GetMapping("/form/new")
    public ModelAndView showCreateForm() {
        ModelAndView mv = new ModelAndView("back/commande/commandeCreate");
        mv.addObject("clients", clientService.getAll());
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
    public String save(@ModelAttribute("commande-form") CommandeBackFormDto form) {
        commandeService.saveBackCommande(form);
        return "redirect:/commandes/list";
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listCommandes() {
        ModelAndView mv = new ModelAndView("list-commandes");
        mv.addObject("commandes", commandeService.getCommandesDispo());
        return mv;
    }
}
