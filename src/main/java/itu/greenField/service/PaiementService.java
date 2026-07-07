package itu.greenField.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import itu.greenField.dto.PaiementLigneDto;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.MvtStock;
import itu.greenField.model.MvtStockFille;
import itu.greenField.model.Paiement;
import itu.greenField.model.PaiementFille;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.StatutPaiement;
import itu.greenField.model.Tresorerie;
import itu.greenField.model.TypeFlux;
import itu.greenField.model.TypeMvt;
import itu.greenField.model.TypePayement;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.MvtStockFilleRepository;
import itu.greenField.repository.MvtStockRepository;
import itu.greenField.repository.PaiementFilleRepository;
import itu.greenField.repository.PaiementRepository;
import itu.greenField.repository.StatutCommandeRepository;
import itu.greenField.repository.TresorerieRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementService {
    private static final BigDecimal ARRONDI_AVANCE = BigDecimal.valueOf(100);

    private final PaiementRepository paiementRepository;
    private final PaiementFilleRepository paiementFilleRepository;
    private final TresorerieRepository tresorerieRepository;
    private final MvtStockRepository mvtStockRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;
    private final CommandesRepository commandesRepository;
    private final CommandesService commandesService;
    private final PointDeVenteService pointDeVenteService;
    private final StatutCommandeService statutCommandeService;
    private final StatutCommandeRepository statutCommandeRepository;

    public List<String> getNumerosTransfertMobileMoney() {
        return List.of("MVola: 034 00 000 01", "Orange Money: 032 00 000 02", "Airtel Money: 033 00 000 03");
    }

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public List<Paiement> findByStatut(StatutPaiement statut) {
        if (statut == null) {
            return findAll();
        }
        return paiementRepository.findByStatut(statut);
    }

    public Paiement findById(Integer id) {
        return paiementRepository.findById(id).orElse(null);
    }

    public Paiement findByCommandeId(Integer commandeId) {
        return paiementRepository.findByCommandeId(commandeId).orElse(null);
    }

    public BigDecimal getMontantTotalCommande(Commandes commande) {
        BigDecimal totalProduits = commande.getTotalGeneral() == null ? BigDecimal.ZERO : commande.getTotalGeneral();
        BigDecimal fraisLivraison = commande.getFraisLivraison() == null ? BigDecimal.ZERO
                : commande.getFraisLivraison();
        return totalProduits.add(fraisLivraison);
    }

    public BigDecimal calculerAvance(Commandes commande) {
        BigDecimal tiers = getMontantTotalCommande(commande).divide(BigDecimal.valueOf(3), 0, RoundingMode.CEILING);
        return tiers.divide(ARRONDI_AVANCE, 0, RoundingMode.CEILING).multiply(ARRONDI_AVANCE);
    }

    public BigDecimal getMontantPaye(Paiement paiement) {
        if (paiement == null) {
            return BigDecimal.ZERO;
        }
        return paiementFilleRepository.findByPaiementId(paiement.getId()).stream()
                .map(PaiementFille::getValeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant(Commandes commande) {
        Paiement paiement = findByCommandeId(commande.getId());
        BigDecimal restant = getMontantTotalCommande(commande).subtract(getMontantPaye(paiement));
        return restant.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : restant;
    }

    public boolean aUnPaiementEnEspece(Paiement paiement) {
        return paiement != null
                && !paiementFilleRepository.findByPaiementIdAndTypePayement(paiement.getId(), TypePayement.Espece)
                        .isEmpty();
    }

    public List<PaiementFille> findFilles(Paiement paiement) {
        if (paiement == null || paiement.getId() == null) {
            return List.of();
        }
        return paiementFilleRepository.findByPaiementId(paiement.getId());
    }

    public BigDecimal getMontantEspece(Paiement paiement) {
        if (paiement == null) {
            return BigDecimal.ZERO;
        }
        return paiementFilleRepository.findByPaiementIdAndTypePayement(paiement.getId(), TypePayement.Espece).stream()
                .map(PaiementFille::getValeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Paiement payerAvance(Integer commandeId, BigDecimal montant) throws Exception {
        Commandes commande = getCommandeOuErreur(commandeId);
        if (findByCommandeId(commandeId) != null) {
            throw new Exception("Cette commande a deja un paiement.");
        }

        BigDecimal avance = calculerAvance(commande);
        verifierMontantExact(montant, avance, "Le montant de l'avance doit etre egal a " + avance + ".");

        Paiement paiement = creerOuRecupererPaiement(commande);
        PaiementFille fille = creerFille(paiement, TypePayement.Mobile_Money, montant);
        paiement.setStatut(StatutPaiement.Reste);
        paiement = paiementRepository.save(paiement);

        creerEntreeTresorerie(commande, fille);
        creerMouvementStockSortie(commande);
        return paiement;
    }

    @Transactional
    public Paiement payerTotalOuReste(Integer commandeId, List<PaiementLigneDto> lignes) throws Exception {
        Commandes commande = getCommandeOuErreur(commandeId);
        Paiement paiementExistant = findByCommandeId(commandeId);
        BigDecimal montantRestant = getMontantRestant(commande);
        verifierMontantExact(somme(lignes), montantRestant, "Le montant saisi doit etre egal au reste a payer.");

        boolean nouveauPaiement = paiementExistant == null;
        Paiement paiement = nouveauPaiement ? creerOuRecupererPaiement(commande) : paiementExistant;
        List<PaiementFille> nouvellesFilles = enregistrerFilles(paiement, lignes);

        boolean contientEspece = nouvellesFilles.stream().anyMatch(f -> f.getTypePayement() == TypePayement.Espece);
        paiement.setStatut(contientEspece ? StatutPaiement.Reste : StatutPaiement.Cloture);
        paiement = paiementRepository.save(paiement);

        nouvellesFilles.stream()
                .filter(f -> f.getTypePayement() != TypePayement.Espece)
                .forEach(f -> creerEntreeTresorerie(commande, f));

        if (nouveauPaiement) {
            creerMouvementStockSortie(commande);
        }
        if (!contientEspece) {
            marquerCommandePayee(commande);
        }
        return paiement;
    }

    @Transactional
    public Paiement confirmerPaiementEspece(Integer paiementId) throws Exception {
        Paiement paiement = paiementRepository.findById(paiementId).orElse(null);
        if (paiement == null) {
            throw new Exception("Paiement introuvable.");
        }

        List<PaiementFille> fillesEspece = paiementFilleRepository.findByPaiementIdAndTypePayement(paiementId,
                TypePayement.Espece);
        if (fillesEspece.isEmpty()) {
            throw new Exception("Aucun paiement en espece a confirmer.");
        }

        for (PaiementFille fille : fillesEspece) {
            creerEntreeTresorerie(paiement.getCommande(), fille);
        }
        paiement.setStatut(StatutPaiement.Cloture);
        paiement = paiementRepository.save(paiement);
        marquerCommandePayee(paiement.getCommande());
        return paiement;
    }

    private Commandes getCommandeOuErreur(Integer commandeId) throws Exception {
        Commandes commande = commandesService.findById(commandeId);
        if (commande == null) {
            throw new Exception("Commande introuvable.");
        }
        return commande;
    }

    private Paiement creerOuRecupererPaiement(Commandes commande) {
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setStatut(StatutPaiement.Cree);
        paiement.setDate(LocalDate.now());
        return paiementRepository.save(paiement);
    }

    private List<PaiementFille> enregistrerFilles(Paiement paiement, List<PaiementLigneDto> lignes) throws Exception {
        List<PaiementFille> filles = new ArrayList<>();
        for (PaiementLigneDto ligne : lignes) {
            if (ligne.getTypePayement() == null) {
                throw new Exception("Le type de paiement est obligatoire.");
            }
            if (ligne.getMontant() == null || ligne.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("Chaque montant doit etre superieur a zero.");
            }
            filles.add(creerFille(paiement, ligne.getTypePayement(), ligne.getMontant()));
        }
        return filles;
    }

    private PaiementFille creerFille(Paiement paiement, TypePayement type, BigDecimal montant) {
        PaiementFille fille = new PaiementFille();
        fille.setPaiement(paiement);
        fille.setTypePayement(type);
        fille.setValeur(montant);
        fille.setDate(LocalDate.now());
        return paiementFilleRepository.save(fille);
    }

    private void creerEntreeTresorerie(Commandes commande, PaiementFille fille) {
        Tresorerie tresorerie = new Tresorerie();
        tresorerie.setCommande(commande);
        tresorerie.setTypeMouvement(TypeFlux.Entree_Vente);
        tresorerie.setMontant(fille.getValeur());
        tresorerie.setDateOperation(LocalDateTime.now());
        tresorerie.setDescription("Paiement " + fille.getTypePayement() + " commande #" + commande.getId());
        tresorerieRepository.save(tresorerie);
    }

    private void creerMouvementStockSortie(Commandes commande) {
        MvtStock mvtStock = new MvtStock();
        mvtStock.setTypeMouvement(TypeMvt.Vente_Client);
        mvtStock.setPointDeVente(getPointDeVenteStock(commande));
        mvtStock.setDateMvt(LocalDateTime.now());
        mvtStock = mvtStockRepository.save(mvtStock);

        for (DetailsCommande detail : commande.getDetailsCommande()) {
            MvtStockFille fille = new MvtStockFille();
            fille.setMvtStock(mvtStock);
            fille.setProduit(detail.getProduit());
            fille.setQuantite(detail.getQuantite());
            mvtStockFilleRepository.save(fille);
        }
    }

    private PointDeVente getPointDeVenteStock(Commandes commande) {
        if (commande.getPointDeVenteRetrait() != null) {
            return commande.getPointDeVenteRetrait();
        }
        return pointDeVenteService.findPointDeVenteById(1);
    }

    private void marquerCommandePayee(Commandes commande) {
        StatutCommande statutPayee = statutCommandeService.findByNom("Payée");
        if (statutPayee != null) {
            commande.setStatutActuel(statutPayee);
            commandesRepository.save(commande);
        }
    }

    private BigDecimal somme(List<PaiementLigneDto> lignes) throws Exception {
        if (lignes == null || lignes.isEmpty()) {
            throw new Exception("Veuillez saisir au moins un paiement.");
        }
        BigDecimal somme = BigDecimal.ZERO;
        for (PaiementLigneDto ligne : lignes) {
            if (ligne.getMontant() != null) {
                somme = somme.add(ligne.getMontant());
            }
        }
        return somme;
    }

    private void verifierMontantExact(BigDecimal montantSaisi, BigDecimal montantAttendu, String message)
            throws Exception {
        if (montantSaisi == null || montantSaisi.compareTo(montantAttendu) != 0) {
            throw new Exception(message);
        }
    }

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
