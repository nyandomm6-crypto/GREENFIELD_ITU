package itu.greenfield.service;

import org.springframework.stereotype.Service;

import itu.greenfield.model.Commandes;
import itu.greenfield.model.HistoriqueStatutCommande;
import itu.greenfield.model.StatutCommande;
import itu.greenfield.repository.HistoriqueStatutCommandeRepository;

@Service
public class HistoriqueStatutCommandeService {
    private final HistoriqueStatutCommandeRepository repository;

    public HistoriqueStatutCommandeService(HistoriqueStatutCommandeRepository repository) {
        this.repository = repository;
    }

    public StatutCommande getCurrentStatutCommande(Commandes cmd) {
        HistoriqueStatutCommande histo = repository.findFirstByCommandeOrderByDatechangementDesc(cmd).orElse(null);
        StatutCommande statut = null;
        if(histo != null)
            statut = histo.getStatutCommande();
        return statut;
    }

    public HistoriqueStatutCommande save(HistoriqueStatutCommande histo) {
        return repository.save(histo);
    }
}
