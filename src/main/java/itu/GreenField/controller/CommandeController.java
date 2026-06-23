package itu.GreenField.controller;

import org.springframework.stereotype.Controller;
import itu.GreenField.service.CommandesService;
import itu.GreenField.service.ClientService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/commandes")
public class CommandeController {
    private CommandesService commandeService;
    private ClientService clientService;

    public CommandeController(CommandesService commandeService, ClientService clientService) {
        this.commandeService = commandeService;
        this.clientService = clientService;
    }

    @GetMapping("/form/new")
    public ModelAndView showCreateForm() {
        ModelAndView mv = new ModelAndView("commandeCreate");
        mv.addObject("clients", clientService.getAll());
        return mv;
    }

    @GetMapping("/form/edit")
    public ModelAndView showEditForm(@RequestParam("id") Integer id) {
        ModelAndView mv = new ModelAndView("commandeEdit");
        mv.addObject("commande", commandeService.getCommandeById(id));
        return mv;
    }

    @PostMapping("/save")
    public String postMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listCommandes() {
        ModelAndView mv = new ModelAndView("list-commandes");
        mv.addObject("commandes", commandeService.getCommandesDispo());
        return mv;
    }
}
