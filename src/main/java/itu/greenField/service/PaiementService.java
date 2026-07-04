package itu.greenField.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import itu.greenField.model.Commandes;
import itu.greenField.model.Paiement;
import itu.greenField.model.PaiementFille;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.StatutPaiement;
import itu.greenField.model.TypePayement;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.PaiementFilleRepository;
import itu.greenField.repository.PaiementRepository;
import itu.greenField.repository.StatutCommandeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final CommandesRepository commandesRepository;
    private final PaiementFilleRepository paiementFilleRepository;
    private final StatutCommandeRepository statutCommandeRepository;

    public BigDecimal resteByCommande(Integer idcommande) {

        Commandes commande = commandesRepository.getById(idcommande);
        BigDecimal somme = BigDecimal.ZERO;

        Paiement paiement = paiementRepository.findByCommande(commande).orElse(null);
        if (paiement == null) {

            paiement = new Paiement();

            paiement.setCommande(commande);

            paiement.setDate(LocalDate.now());

            paiement.setStatut(StatutPaiement.Cree);

            paiement = paiementRepository.save(paiement);

        }

        if (paiement != null && paiement.getFilles() != null) {
            for (PaiementFille pf : paiement.getFilles()) {
                if (pf.getValeur() != null) {
                    somme = somme.add(pf.getValeur());
                }
            }
        }

        BigDecimal total = commande.getTotalGeneral();

        if (total == null) {
            return BigDecimal.ZERO;
        }

        return total.subtract(somme);
    }

    public void ajouterPayement(List<TypePayement> types, List<BigDecimal> valeurs, Integer idCommande) {
        Commandes commande = commandesRepository.findById(idCommande)

                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        Paiement paiement = paiementRepository.findByCommande(commande).orElse(null);

        if (paiement == null) {

            paiement = new Paiement();

            paiement.setCommande(commande);

            paiement.setDate(LocalDate.now());
            paiement.setStatut(StatutPaiement.Cree);

            paiement = paiementRepository.save(paiement);

        }

        BigDecimal somme = BigDecimal.ZERO;

        for (int i = 0; i < valeurs.size(); i++) {

            if (valeurs.get(i) != null) {

                PaiementFille pf = new PaiementFille();

                pf.setPaiement(paiement);

                pf.setTypePayement(types.get(i));

                pf.setValeur(valeurs.get(i));

                paiementFilleRepository.save(pf);

                somme = somme.add(valeurs.get(i));

            }

        }

        // ================= UPDATE STATUT =================

        BigDecimal reste = this.resteByCommande(commande.getId());

        if (reste.compareTo(BigDecimal.ZERO) <= 0) {

            StatutCommande statut = statutCommandeRepository.findByNom("Payée")

                    .orElseThrow();

            commande.setStatutActuel(statut);

            commandesRepository.save(commande);

        }

        // update statut payement
        BigDecimal valeur = this.resteByCommande(commande.getId());

        if (valeur.compareTo(BigDecimal.ZERO) <= 0) {
            paiement.setStatut(StatutPaiement.Cloture);
            paiementRepository.save(paiement);
        }
    }
}