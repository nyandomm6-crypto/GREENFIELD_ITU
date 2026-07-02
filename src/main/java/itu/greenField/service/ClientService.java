package itu.greenField.service;

import org.springframework.stereotype.Service;
import itu.greenField.model.Client;
import itu.greenField.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientsRepository;

    public ClientService(ClientRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    public Client getClientById(Integer id) {
        return clientsRepository.findById(id).orElse(null);
    }

    public List<Client> getAll(){
        return clientsRepository.findAll();
    }

    public List<Client> searchClientsByNometPrenom(String nom, String prenom) {
        return clientsRepository.findClientsByNometPrenom(nom, prenom);
    }

    public String getSearchedClientsJson(String nom, String prenom) {
        List<Client> clients = clientsRepository.findClientsByNometPrenom(nom, prenom);
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            jsonBuilder.append(ClientToJson(client));
            if (i < clients.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    public String getSearchedClientsJson(String query) {
        List<Client> clients = clientsRepository.findClientsByWord(query);
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            jsonBuilder.append(ClientToJson(client));
            if (i < clients.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    public String ClientToJson(Client client) {
        if (client == null) {
            return "{}";
        }
        return String.format("{\"id\": %d, \"nom\": \"%s\", \"prenom\": \"%s\", \"mail\": \"%s\"}",
                client.getId(), client.getNom(), client.getPrenom(), client.getMail());
    }

    public Client saveClient(Client client) {
        return clientsRepository.save(client);
    }
}
