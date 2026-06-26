package itu.GreenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import itu.GreenField.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(value = "/search", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String searchClients(
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "prenom", required = false) String prenom) {

        System.out.println("--> NOM REÇU : '" + nom + "'");
        System.out.println("--> PRÉNOM REÇU : '" + prenom + "'");
        return clientService.getSearchedClientsJson(nom, prenom);
    }
}
