package itu.GreenField.service;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Commandes;
import itu.GreenField.model.HistoriqueStatutCommande;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.repository.HistoriqueStatutCommandeRepository;

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
