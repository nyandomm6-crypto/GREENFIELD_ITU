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
import itu.greenField.model.LivraisonFille;
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
import itu.greenField.repository.LivraisonFilleRepository;
import itu.greenField.repository.MvtStockFilleRepository;
import itu.greenField.repository.MvtStockRepository;
import itu.greenField.repository.PaiementFilleRepository;
import itu.greenField.repository.PaiementRepository;
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
    private final LivraisonFilleRepository livraisonFilleRepository;
    private final StatutCommandeService statutCommandeService;

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

    /** Paiements des commandes créées par le point de vente donné (par code) — espace caissier. */
    public List<Paiement> findByPointDeVente(String code) {
        if (code == null || code.isBlank()) {
            return new ArrayList<>();
        }
        return paiementRepository.findByCommande_PointDeVenteCreateur_CodeOrderByIdDesc(code);
    }

    /** Paiements d'un client — front office. */
    public List<Paiement> findByClient(Integer clientId) {
        if (clientId == null) {
            return new ArrayList<>();
        }
        return paiementRepository.findByCommande_Client_IdOrderByIdDesc(clientId);
    }

    /** Vrai si la commande appartient bien au client donné. */
    public boolean appartientAuClient(Commandes commande, Integer clientId) {
        return commande != null && clientId != null && commande.getClient() != null
                && clientId.equals(commande.getClient().getId());
    }

    /**
     * Création d'un paiement par un caissier (côté back-end). Vérifie d'abord que
     * la commande appartient bien au point de vente du caissier, puis réutilise la
     * logique de paiement existante (total ou reste à payer).
     */
    @Transactional
    public Paiement creerPaiementCaissier(Integer commandeId, List<PaiementLigneDto> lignes, String pdvCode)
            throws Exception {
        verifierCommandeDuPointDeVente(commandeId, pdvCode);
        return payerTotalOuReste(commandeId, lignes);
    }

    /** Avance payée par le caissier, limitée aux commandes de son point de vente. */
    @Transactional
    public Paiement payerAvanceCaissier(Integer commandeId, BigDecimal montant, String pdvCode) throws Exception {
        verifierCommandeDuPointDeVente(commandeId, pdvCode);
        return payerAvance(commandeId, montant);
    }

    /** Confirmation manuelle par le caissier, limitée aux paiements de son point de vente. */
    @Transactional
    public Paiement confirmerPaiementCaissier(Integer paiementId, Integer filleId, BigDecimal montantRecu,
            String pdvCode) throws Exception {
        Paiement paiement = paiementRepository.findById(paiementId).orElse(null);
        if (paiement == null) {
            throw new Exception("Paiement introuvable.");
        }
        verifierCommandeDuPointDeVente(paiement.getCommande() == null ? null : paiement.getCommande().getId(), pdvCode);
        return confirmerPaiement(paiementId, filleId, montantRecu);
    }

    /**
     * Vérifie que la commande a bien été créée par le point de vente donné. Un
     * caissier n'encaisse que les commandes de son point de vente créateur : c'est
     * la même règle que la liste et le détail des commandes de l'espace caissier,
     * le point de retrait pouvant être une autre boutique.
     */
    public Commandes verifierCommandeDuPointDeVente(Integer commandeId, String pdvCode) throws Exception {
        Commandes commande = getCommandeOuErreur(commandeId);
        if (pdvCode == null || commande.getPointDeVenteCreateur() == null
                || !pdvCode.equals(commande.getPointDeVenteCreateur().getCode())) {
            throw new Exception("Cette commande n'appartient pas à votre point de vente.");
        }
        return commande;
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

    /** Types encaissés seulement après confirmation manuelle (caissier ou admin). */
    public static boolean estAConfirmer(TypePayement type) {
        return TypePayement.Espece.equals(type) || TypePayement.Mobile_Money.equals(type);
    }

    public List<PaiementFille> findFilles(Paiement paiement) {
        if (paiement == null || paiement.getId() == null) {
            return List.of();
        }
        return paiementFilleRepository.findByPaiementId(paiement.getId());
    }

    /** Lignes encore en attente de confirmation manuelle. */
    public List<PaiementFille> findFillesEnAttente(Paiement paiement) {
        return findFilles(paiement).stream().filter(PaiementFille::isEnAttente).toList();
    }

    public boolean aDesLignesEnAttente(Paiement paiement) {
        return !findFillesEnAttente(paiement).isEmpty();
    }

    /** Montant saisi mais pas encore encaissé (en attente de confirmation). */
    public BigDecimal getMontantEnAttente(Paiement paiement) {
        return findFillesEnAttente(paiement).stream()
                .map(PaiementFille::getValeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Montant réellement encaissé (une entrée trésorerie existe pour ces lignes). */
    public BigDecimal getMontantConfirme(Paiement paiement) {
        return findFilles(paiement).stream()
                .filter(f -> !f.isEnAttente())
                .map(PaiementFille::getValeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Le paiement est complet : tout le montant de la commande est saisi et
     * toutes les lignes sont confirmées. C'est la seule condition qui déclenche
     * la sortie de stock.
     */
    public boolean estCompletEtConfirme(Paiement paiement) {
        if (paiement == null || paiement.getCommande() == null) {
            return false;
        }
        return !aDesLignesEnAttente(paiement)
                && getMontantRestant(paiement.getCommande()).compareTo(BigDecimal.ZERO) <= 0;
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
        creerFille(paiement, TypePayement.Mobile_Money, montant);
        // Mobile Money : ni entrée trésorerie ni sortie de stock tant que
        // l'avance n'est pas confirmée manuellement.
        return rafraichirStatut(paiement);
    }

    @Transactional
    public Paiement payerTotalOuReste(Integer commandeId, List<PaiementLigneDto> lignes) throws Exception {
        Commandes commande = getCommandeOuErreur(commandeId);
        Paiement paiementExistant = findByCommandeId(commandeId);

        // On ne peut pas payer le reste tant que ce qui a déjà été versé (l'avance
        // par exemple) n'a pas été confirmé : il faut d'abord saisir le montant
        // réellement reçu et confirmer la ligne.
        if (aDesLignesEnAttente(paiementExistant)) {
            throw new Exception(
                    "Veuillez d'abord confirmer les encaissements en attente (saisie du montant recu) "
                            + "avant de payer le reste.");
        }

        BigDecimal montantRestant = getMontantRestant(commande);
        verifierMontantExact(somme(lignes), montantRestant, "Le montant saisi doit etre egal au reste a payer.");

        Paiement paiement = paiementExistant == null ? creerOuRecupererPaiement(commande) : paiementExistant;
        List<PaiementFille> nouvellesFilles = enregistrerFilles(paiement, lignes);

        // Seuls les types sans confirmation (carte de fidélité) sont encaissés
        // immédiatement ; espèce et mobile money attendent une confirmation.
        nouvellesFilles.stream()
                .filter(f -> !f.isEnAttente())
                .forEach(f -> creerEntreeTresorerie(commande, f));

        return rafraichirStatut(paiement);
    }

    /**
     * Confirme manuellement une ligne en attente (espèce, mobile money) : c'est
     * ce geste qui crée l'entrée de trésorerie correspondante.
     *
     * En Mobile Money, l'opérateur reçoit le message de transfert et saisit le
     * montant réellement reçu ; c'est ce montant qui est encaissé. Si le
     * transfert ne correspond pas à ce qui était annoncé, l'opérateur ne
     * confirme simplement pas la ligne.
     *
     * @param filleId     ligne à confirmer.
     * @param montantRecu montant réellement reçu, ou null pour reprendre le
     *                    montant annoncé sur la ligne.
     */
    @Transactional
    public Paiement confirmerPaiement(Integer paiementId, Integer filleId, BigDecimal montantRecu) throws Exception {
        Paiement paiement = paiementRepository.findById(paiementId).orElse(null);
        if (paiement == null) {
            throw new Exception("Paiement introuvable.");
        }
        if (filleId == null) {
            throw new Exception("Veuillez indiquer la ligne de paiement a confirmer.");
        }

        PaiementFille fille = findFillesEnAttente(paiement).stream()
                .filter(f -> filleId.equals(f.getId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Aucun paiement en attente de confirmation."));

        if (montantRecu != null) {
            if (montantRecu.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("Le montant recu doit etre superieur a zero.");
            }
            // Le montant encaissé est celui réellement reçu : la ligne et la
            // trésorerie ne peuvent pas diverger.
            fille.setValeur(montantRecu);
        }

        creerEntreeTresorerie(paiement.getCommande(), fille);
        fille.setConfirme(true);
        paiementFilleRepository.save(fille);

        return rafraichirStatut(paiement);
    }

    /**
     * Recalcule le statut du paiement. La sortie de stock et le passage de la
     * commande à « Payée » n'ont lieu qu'une fois le paiement complet ET
     * entièrement confirmé, au moment où le paiement se clôture.
     */
    private Paiement rafraichirStatut(Paiement paiement) throws Exception {
        boolean dejaCloture = StatutPaiement.Cloture.equals(paiement.getStatut());
        boolean complet = estCompletEtConfirme(paiement);

        paiement.setStatut(complet ? StatutPaiement.Cloture : StatutPaiement.Reste);
        paiement = paiementRepository.save(paiement);

        if (complet && !dejaCloture) {
            creerMouvementStockSortie(paiement.getCommande());
            marquerCommandePayee(paiement.getCommande());
        }
        return paiement;
    }

    private Commandes getCommandeOuErreur(Integer commandeId) throws Exception {
        if (commandeId == null) {
            throw new Exception("Commande introuvable.");
        }
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
        // Espèce et mobile money restent à confirmer ; les autres sont encaissés d'office.
        fille.setConfirme(!estAConfirmer(type));
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

    private void creerMouvementStockSortie(Commandes commande) throws Exception {
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

    /**
     * Point de vente d'où sort le stock : la boutique de retrait quand le client
     * vient chercher sa commande, sinon celle du livreur affecté à la livraison,
     * puisque c'est de son point de vente que part la marchandise.
     */
    private PointDeVente getPointDeVenteStock(Commandes commande) throws Exception {
        if (commande.getPointDeVenteRetrait() != null) {
            return commande.getPointDeVenteRetrait();
        }
        PointDeVente pdvLivreur = getPointDeVenteLivreur(commande);
        if (pdvLivreur == null) {
            throw new Exception("Impossible de determiner le point de vente pour la sortie de stock : "
                    + "la commande n'a ni point de vente de retrait, ni livreur rattache a un point de vente.");
        }
        return pdvLivreur;
    }

    /** Point de vente du livreur affecté à la livraison de cette commande. */
    private PointDeVente getPointDeVenteLivreur(Commandes commande) {
        return livraisonFilleRepository.findByCommande(commande).stream()
                .map(LivraisonFille::getLivraison)
                .filter(livraison -> livraison != null && livraison.getLivreur() != null)
                .map(livraison -> livraison.getLivreur().getPointDeVente())
                .filter(pdv -> pdv != null)
                .findFirst()
                .orElse(null);
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

}
