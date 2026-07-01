package itu.GreenField.service;

import itu.GreenField.dto.*;
import itu.GreenField.model.*;
import itu.GreenField.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransfertService {

        private final TransfertsRepository transfertsRepository;
        private final TransfertsFilleRepository transfertsFilleRepository;
        private final StockRepository stockRepository;
        private final MvtStockRepository mvtStockRepository;
        private final MvtStockFilleRepository mvtStockFilleRepository;
        private final PointDeVenteRepository pointDeVenteRepository;
        private final ProduitRepository produitRepository;
        private final DemandeStockRepository demandeStockRepository;
        private final DemandeStockFilleRepository demandeStockFilleRepository;

        // ✅ CONSTRUCTEUR MANUEL (remplace @RequiredArgsConstructor)
        public TransfertService(TransfertsRepository transfertsRepository,
                        TransfertsFilleRepository transfertsFilleRepository,
                        StockRepository stockRepository,
                        MvtStockRepository mvtStockRepository,
                        MvtStockFilleRepository mvtStockFilleRepository,
                        PointDeVenteRepository pointDeVenteRepository,
                        ProduitRepository produitRepository,
                        DemandeStockRepository demandeStockRepository,
                        DemandeStockFilleRepository demandeStockFilleRepository) {
                this.transfertsRepository = transfertsRepository;
                this.transfertsFilleRepository = transfertsFilleRepository;
                this.stockRepository = stockRepository;
                this.mvtStockRepository = mvtStockRepository;
                this.mvtStockFilleRepository = mvtStockFilleRepository;
                this.pointDeVenteRepository = pointDeVenteRepository;
                this.produitRepository = produitRepository;
                this.demandeStockRepository = demandeStockRepository;
                this.demandeStockFilleRepository = demandeStockFilleRepository;
        }

        // =====================================================
        // 1) DEMANDE TRANSFERT
        // =====================================================
        @Transactional
        public DemandeStock demandeTransfert(DemandeTransfertRequest req) {
                PointDeVente pointDeVenteDemandeur = pointDeVenteRepository
                                .findByCode(req.getCodePointDeVenteDemandeur())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Point de vente demandeur introuvable : "
                                                                + req.getCodePointDeVenteDemandeur()));

                DemandeStock demande = new DemandeStock();
                demande.setPointDeVente(pointDeVenteDemandeur);
                demande.setDateDemande(LocalDateTime.now());
                demande.setDateCible(req.getDateCible());
                demande = demandeStockRepository.save(demande);

                for (ProduitQuantiteDTO pq : req.getProduits()) {
                        Produit produit = produitRepository.findById(pq.getIdProduit())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Produit introuvable : " + pq.getIdProduit()));

                        DemandeStockFille ligne = new DemandeStockFille();
                        ligne.setDemandeStock(demande);
                        ligne.setProduit(produit);
                        ligne.setQuantite(pq.getQuantite());
                        demandeStockFilleRepository.save(ligne);
                }

                return demande;
        }

        // =====================================================
        // 2) FONCTION : find point de vente avec le plus de stock
        // =====================================================
        public String trouverPointDeVenteAvecPlusDeStock(List<ProduitQuantiteDTO> produitsDemandes,
                        String codePointDeVenteCible) {
                List<Integer> idsProduits = produitsDemandes.stream()
                                .map(ProduitQuantiteDTO::getIdProduit)
                                .collect(Collectors.toList());

                List<PointDeVenteStockProjection> candidats = stockRepository
                                .trouverStockParPointDeVentePourProduits(idsProduits, codePointDeVenteCible);

                return candidats.stream()
                                .filter(c -> c.getStockTotal() != null && c.getStockTotal() > 0)
                                .map(PointDeVenteStockProjection::getCodePointDeVente)
                                .findFirst()
                                .orElse(null);
        }

        // =====================================================
        // 3) CREER TRANSFERT
        // =====================================================
        @Transactional
        public TransfertDetailResponse creerTransfert(CreerTransfertRequest req) {
                if (req.getProduits() == null || req.getProduits().isEmpty()) {
                        throw new IllegalArgumentException("Aucun produit demandé pour ce transfert.");
                }

                String codeSource = trouverPointDeVenteAvecPlusDeStock(req.getProduits(),
                                req.getCodePointDeVenteCible());
                if (codeSource == null) {
                        throw new IllegalStateException(
                                        "Aucun point de vente n'a de stock disponible pour les produits demandés.");
                }

                PointDeVente source = pointDeVenteRepository.findByCode(codeSource)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Point de vente source introuvable : " + codeSource));
                PointDeVente cible = pointDeVenteRepository.findByCode(req.getCodePointDeVenteCible())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Point de vente cible introuvable : "
                                                                + req.getCodePointDeVenteCible()));

                Transferts transfert = new Transferts();
                transfert.setPointDeVenteSource(source);
                transfert.setPointDeVenteCible(cible);
                transfert.setStatutTransfert(StatutTransfert.En_cours);
                transfert.setDateTransfert(LocalDateTime.now());
                transfert = transfertsRepository.save(transfert);

                MvtStock mvtSortie = new MvtStock();
                mvtSortie.setTypeMouvement(TypeMvt.Sortie_Transfert);
                mvtSortie.setPointDeVente(source);
                mvtSortie.setDateMvt(LocalDateTime.now());
                mvtSortie = mvtStockRepository.save(mvtSortie);

                MvtStock mvtEntree = new MvtStock();
                mvtEntree.setTypeMouvement(TypeMvt.Entree_Boutique);
                mvtEntree.setPointDeVente(cible);
                mvtEntree.setDateMvt(LocalDateTime.now());
                mvtEntree = mvtStockRepository.save(mvtEntree);

                List<TransfertDetailResponse.LigneTransfertDTO> lignesReponse = new ArrayList<>();
                boolean auMoinsUnProduitTransfere = false;

                for (ProduitQuantiteDTO pq : req.getProduits()) {
                        Produit produit = produitRepository.findById(pq.getIdProduit())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Produit introuvable : " + pq.getIdProduit()));

                        Integer stockDisponible = stockRepository.trouverStockDisponible(pq.getIdProduit(), codeSource);
                        if (stockDisponible == null)
                                stockDisponible = 0;

                        int quantiteDemandee = pq.getQuantite();
                        int quantiteATransferer = Math.min(quantiteDemandee, stockDisponible);
                        boolean partiel = quantiteATransferer < quantiteDemandee;

                        if (quantiteATransferer <= 0) {
                                lignesReponse.add(new TransfertDetailResponse.LigneTransfertDTO(
                                                produit.getId(), produit.getNom(), quantiteDemandee, 0, true));
                                continue;
                        }

                        MvtStockFille ligneSortie = new MvtStockFille();
                        ligneSortie.setMvtStock(mvtSortie);
                        ligneSortie.setProduit(produit);
                        ligneSortie.setQuantite(quantiteATransferer);
                        mvtStockFilleRepository.save(ligneSortie);

                        MvtStockFille ligneEntree = new MvtStockFille();
                        ligneEntree.setMvtStock(mvtEntree);
                        ligneEntree.setProduit(produit);
                        ligneEntree.setQuantite(quantiteATransferer);
                        mvtStockFilleRepository.save(ligneEntree);

                        TransfertsFille ligneTransfert = new TransfertsFille();
                        ligneTransfert.setTransfert(transfert);
                        ligneTransfert.setProduit(produit);
                        ligneTransfert.setQuantite(quantiteATransferer);
                        transfertsFilleRepository.save(ligneTransfert);

                        lignesReponse.add(new TransfertDetailResponse.LigneTransfertDTO(
                                        produit.getId(), produit.getNom(), quantiteDemandee, quantiteATransferer,
                                        partiel));

                        auMoinsUnProduitTransfere = true;
                }

                if (!auMoinsUnProduitTransfere) {
                        throw new IllegalStateException(
                                        "Le stock du point de vente source est épuisé pour tous les produits demandés.");
                }

                transfert.setStatutTransfert(StatutTransfert.Termine);
                transfert = transfertsRepository.save(transfert);

                return mapVersDetailResponse(transfert, lignesReponse);
        }

        // =====================================================
        // 4) LISTE TRANSFERT
        // =====================================================
        public List<TransfertDetailResponse> listerTransferts(LocalDateTime dateDebut,
                        LocalDateTime dateFin,
                        String codePointDeVente) {
                return transfertsRepository.rechercherAvecFiltres(dateDebut, dateFin, codePointDeVente)
                                .stream()
                                .map(t -> mapVersDetailResponse(t, construireLignesReponse(t)))
                                .collect(Collectors.toList());
        }

        // =====================================================
        // 5) DETAIL TRANSFERT
        // =====================================================
        public TransfertDetailResponse detailTransfert(Long idTransfert) {
                Transferts transfert = transfertsRepository.findById(idTransfert)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Transfert introuvable : " + idTransfert));
                return mapVersDetailResponse(transfert, construireLignesReponse(transfert));
        }

        // ---------------------------------------------------------
        // Helpers de mapping
        // ---------------------------------------------------------
        private List<TransfertDetailResponse.LigneTransfertDTO> construireLignesReponse(Transferts transfert) {
                return transfertsFilleRepository.findByTransfertId(transfert.getId())
                                .stream()
                                .map(l -> new TransfertDetailResponse.LigneTransfertDTO(
                                                l.getProduit().getId(),
                                                l.getProduit().getNom(),
                                                l.getQuantite(),
                                                l.getQuantite(),
                                                false))
                                .collect(Collectors.toList());
        }

        private TransfertDetailResponse mapVersDetailResponse(Transferts t,
                        List<TransfertDetailResponse.LigneTransfertDTO> lignes) {
                return new TransfertDetailResponse(
                                t.getId(),
                                t.getPointDeVenteSource() != null ? t.getPointDeVenteSource().getCode() : null,
                                t.getPointDeVenteCible() != null ? t.getPointDeVenteCible().getCode() : null,
                                t.getStatutTransfert() != null ? t.getStatutTransfert().name() : null,
                                t.getDateTransfert(),
                                lignes);
        }
}