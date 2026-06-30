package itu.GreenField.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.Query;

import itu.GreenField.dto.CommandeBackFilterDto;
import itu.GreenField.dto.CommandeBackFormDto;
import itu.GreenField.dto.DetailCommandeBackDto;

import itu.GreenField.model.Client;
import itu.GreenField.model.Commandes;
import itu.GreenField.model.ModeReception;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.model.Produit;
import itu.GreenField.model.StatutCommande;
import itu.GreenField.model.TypeCommande;
import itu.GreenField.model.DetailsCommande;
import itu.GreenField.model.HistoriqueStatutCommande;
import itu.GreenField.model.ProvinceLivraison;
import itu.GreenField.model.FraisLivraison;

import itu.GreenField.repository.CommandesRepository;
import itu.GreenField.repository.DetailsCommandeRepository;

@Service
public class CommandesService {
    @PersistenceContext
    private EntityManager em;
    private final ProduitService produitService;
    private final CommandesRepository commandesRepository;
    private final ClientService clientService;
    private final PointDeVenteService pointDeVenteService;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final StatutCommandeService statutCommandeService;
    private final HistoriqueStatutCommandeService historiqueStatutCommandeService;
    private final ProvinceLivraisonService provinceLivraisonService;
    private final FraisLivraisonService fraisLivraisonService;

    public CommandesService(ProduitService produitService, CommandesRepository commandesRepository,
            ClientService clientService, PointDeVenteService pointDeVenteService,
            DetailsCommandeRepository detailsCommandeRepository,
            StatutCommandeService statutCommandeService,
            HistoriqueStatutCommandeService historiqueStatutCommandeService,
            ProvinceLivraisonService provinceLivraisonService,
            FraisLivraisonService fraisLivraisonService
        ) {
        this.produitService = produitService;
        this.commandesRepository = commandesRepository;
        this.clientService = clientService;
        this.pointDeVenteService = pointDeVenteService;
        this.detailsCommandeRepository = detailsCommandeRepository;
        this.statutCommandeService = statutCommandeService;
        this.historiqueStatutCommandeService = historiqueStatutCommandeService;
        this.provinceLivraisonService = provinceLivraisonService;
        this.fraisLivraisonService = fraisLivraisonService;
    }

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findDispoCommandes();
    }

    public Commandes getCommandeById(Integer id) {
        return commandesRepository.findById(id).orElse(null);
    }

    public Page<Commandes> getCommandesPagine(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commandesRepository.findAllPaginated(pageable);
    }

    public void delete(Commandes cmd) throws Exception {
        //throw new Exception();
        commandesRepository.delete(cmd);
    }

    public Commandes findById(Integer id){
        return commandesRepository.findById(id).orElse(null);
    }

    public void checkIfUpdatable(Commandes cmd) throws Exception{
        StatutCommande currentStatut = cmd.getStatutActuel();
            if(currentStatut.getNom().equals("Livrée")){
                throw new Exception("Une commande livrée ne peut plus être modifiée!");
            }
            if(currentStatut.getNom().equals("Anulée")){
                throw new Exception("Une commande anulée ne peut plus être modifiée!");
            }
    }

    @Transactional
    public Commandes saveBackCommande(CommandeBackFormDto commandeFormDto) throws Exception {
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

        ModeReception modeReception = ModeReception.fromString(commandeFormDto.getModeReception());
        TypeCommande typeCommande = TypeCommande.En_boutique;
        
        Commandes commande = null;
        HistoriqueStatutCommande hist = null;
        if (commandeFormDto.getCommandeId() != null) {
            commande = commandesRepository.findById(commandeFormDto.getCommandeId()).orElse(null);
            if (commande == null) {
                throw new Exception("Commande introuvable avec l'ID: " + commandeFormDto.getCommandeId());
            }
            StatutCommande currentStatut = commande.getStatutActuel();
            if(currentStatut.getNom().equals("Livrée")){
                throw new Exception("Une commande livrée ne peut plus être modifiée!");
            }
            if(currentStatut.getNom().equals("Anulée")){
                throw new Exception("Une commande anulée ne peut plus être modifiée!");
            }
            detailsCommandeRepository.deleteAll(commande.getDetailsCommande());
        } else {
            commande = new Commandes();
            commande.setClient(client);
            commande.setDatecommande(commandeFormDto.getSqlTypeOfDate());
            commande.setTypeCommande(typeCommande);
            
            StatutCommande statusCommande = statutCommandeService.findByNom("Créée");
            if (statusCommande == null)
                throw new Exception("Statut commande \"Créée\" n'existe pas");
            hist = new HistoriqueStatutCommande();
            hist.setStatutCommande(statusCommande);
            hist.setCommande(commande);
            hist.setDatechangement(commandeFormDto.getSqlTypeOfDate());
            commande.setStatutActuel(statusCommande);
        }

        commande.setModeReception(modeReception);
        commande.setHeureReceptionDebut(commandeFormDto.getSqlTypeOfHeureReceptionDebut());
        commande.setHeureReceptionFin(commandeFormDto.getSqlTypeOfHeureReceptionFin());
        commande.setAdresseLivraison(commandeFormDto.getAddress());

        /* Static pour le moment */
        if (commandeFormDto.getAddress() == null || modeReception == ModeReception.Retrait_Boutique) {
            PointDeVente pdv = pointDeVenteService.findPointDeVenteById(1);
            commande.setPointDeVenteRetrait(pdv);
            commande.setAdresseLivraison(null);
        } else {
            ProvinceLivraison provinceLivraison = provinceLivraisonService.getProvinceById(commandeFormDto.getProvinceId());
            commande.setProvinceLivraison(provinceLivraison);
        }

        int qteTotal = 0;
        BigDecimal prixTotal = BigDecimal.ZERO;
        BigDecimal poidsTotal = BigDecimal.ZERO;

        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        commande.setPoidsTotal(poidsTotal);

        commande = commandesRepository.save(commande);
        int nbLines = commandeFormDto.getDetailsCommande().size();
        for (int i = 0; i < nbLines; i++) {
            DetailCommandeBackDto detailDto = commandeFormDto.getDetailsCommande().get(i);
            String matricule = detailDto.getProduitMatricule();
            Integer quantite = detailDto.getQuantite();
            DetailsCommande detail = new DetailsCommande();
            Produit produit = produitService.findProduitByMatricule(matricule);
            detail.setCommande(commande);
            detail.setProduit(produit);
            detail.setQuantite(quantite);
            detail.setPuAuMomentAchat(produit.getPu());
            detailsCommandeRepository.save(detail);

            qteTotal += quantite;
            prixTotal = prixTotal.add((BigDecimal.valueOf(quantite)).multiply(produit.getPu()));
            poidsTotal = poidsTotal.add((BigDecimal.valueOf(quantite)).multiply(produit.getPoids()));
        }

        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        commande.setPoidsTotal(poidsTotal);

        commande.setFraisLivraison(BigDecimal.ZERO);
        if (modeReception == ModeReception.Livraison_Domicile) {
            FraisLivraison fraisLivraison = fraisLivraisonService.calculateFraisLivraison(commande.getProvinceLivraison().getId(), poidsTotal.doubleValue());
            commande.setFraisLivraison(fraisLivraison.getMontant());   
        }

        commande = commandesRepository.save(commande);

        if(hist != null)
            historiqueStatutCommandeService.save(hist);

        return commande;
    }

    public Page<Commandes> findWithDynamicFilters(CommandeBackFilterDto filter) {
        int page = filter.getPageNumber();
        int size = filter.getLineNumber();
        Pageable pageable = PageRequest.of(page - 1, size);
        StringBuilder sb = new StringBuilder("SELECT * FROM commandes c WHERE 1 = 1 ");
        StringBuilder sbCount = new StringBuilder("SELECT count(*) FROM commandes c WHERE 1 = 1 ");
        Map<String, Object> params = new HashMap<>();

        // 1. Filtre Statut (statutcommande)
        if (filter.getStatutCommande() != null && !filter.getStatutCommande().isEmpty()) {
            sb.append("AND c.statutcommande = :statut ");
            sbCount.append("AND c.statutcommande = :statut ");
            params.put("statut", filter.getStatutCommande());
        }

        // 2. Filtre Mode Réception (mode_reception)
        if (filter.getModeReception() != null && !filter.getModeReception().isEmpty()) {
            sb.append("AND c.mode_reception = :mode ");
            sbCount.append("AND c.mode_reception = :mode ");
            params.put("mode", filter.getModeReception());
        }

        // 3. Filtre Multi-Clients (idclient)
        if (filter.getClientId() != null && !filter.getClientId().isEmpty()) {
            sb.append("AND c.idclient IN (:clients) ");
            sbCount.append("AND c.idclient IN (:clients) ");
            params.put("clients", filter.getClientId());
        }

        // 3.5. Filtre Type Commande (type_commande)
        if (filter.getTypeCommande() != null && !filter.getTypeCommande().isEmpty()) {
            sb.append("AND c.type_commande = :type ");
            sbCount.append("AND c.type_commande = :type ");
            params.put("type", filter.getTypeCommande());
        }

        // 4. Boucle pour les DATES dynamiques (datecommande, heure_reception_debut,
        // heure_reception_fin)
        if (filter.getTypeFiltreDate() != null) {
            for (int i = 0; i < filter.getTypeFiltreDate().size(); i++) {
                String colonne = filter.getTypeFiltreDate().get(i);
                String opSign = filter.getOperateurDate().get(i); // Utilise directement "=", "<", ">=", etc.
                String paramName = "dateVal_" + i;

                sb.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                sbCount.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                params.put(paramName, filter.getDateValue().get(i));
            }
        }

        // 5. Boucle pour les NOMBRES dynamiques (total_produits, total_general,
        // frais_livraison)
        if (filter.getTypeFiltreNombre() != null) {
            for (int i = 0; i < filter.getTypeFiltreNombre().size(); i++) {
                String colonne = filter.getTypeFiltreNombre().get(i);
                String opSign = filter.getOperateurNombre().get(i); // Utilise directement "=", "<", etc.
                String paramName = "numVal_" + i;

                sb.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                sbCount.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                params.put(paramName, filter.getNombreValue().get(i));
            }
        }

        Query query = em.createNativeQuery(sb.toString(), Commandes.class);
        Query countQuery = em.createNativeQuery(sbCount.toString());

        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Calcul du total d'éléments correspondants aux filtres
        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Commandes> resultList = query.getResultList();

        return new PageImpl<Commandes>(resultList, pageable, total);
    }

}
