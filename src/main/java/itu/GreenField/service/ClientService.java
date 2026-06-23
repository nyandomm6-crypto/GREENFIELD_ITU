package itu.GreenField.service;

import org.springframework.stereotype.Service;
import itu.GreenField.model.Client;
import itu.GreenField.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService {
    private ClientRepository clientsRepository;

    public ClientService(ClientRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    public Client getClientById(Integer id) {
        return clientsRepository.findById(id).orElse(null);
    }

    public List<Client> getAll(){
        return clientsRepository.findAll();
    }
}
