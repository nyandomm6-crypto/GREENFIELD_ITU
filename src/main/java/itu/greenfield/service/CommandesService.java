package itu.GreenField.service;

import java.util.List;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Commandes;
import itu.GreenField.repository.CommandesRepository;

@Service
public class CommandesService {
    private CommandesRepository commandesRepository;

    public CommandesService(CommandesRepository commandesRepository) {
        this.commandesRepository = commandesRepository;
    }

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findAll();
    }

}
