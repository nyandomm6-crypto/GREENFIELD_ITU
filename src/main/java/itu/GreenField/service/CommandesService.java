package itu.GreenField.service;

import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import itu.GreenField.dto.CommandeFormDto;
import itu.GreenField.model.Client;
import itu.GreenField.model.Commandes;
import itu.GreenField.repository.CommandesRepository;

@Service
public class CommandesService {
    private final CommandesRepository commandesRepository;
    private final ClientService clientService;

    public CommandesService(CommandesRepository commandesRepository, ClientService clientService) {
        this.commandesRepository = commandesRepository;
        this.clientService = clientService;
    }

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findDispoCommandes();
    }

    public Commandes getCommandeById(Integer id) {
        return commandesRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveCommande(CommandeFormDto commandeFormDto) {
        Integer clientId = commandeFormDto.getClientId();
        Client client = null;
        if (clientId != null) {
            client = clientService.getClientById(clientId);
        } else {
            client = new Client();
            client.setNom(commandeFormDto.getClientNom());
            client.setPrenom(commandeFormDto.getClientPrenom());
            client = clientService.saveClient(client);
        }

        Commandes commande = new Commandes();
        commande.setClient(client);
        commandesRepository.save(commande);
    }

}
