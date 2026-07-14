package itu.greenField.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import itu.greenField.repository.MvtStockFilleRepository;
import itu.greenField.repository.MvtStockRepository;
import itu.greenField.model.MvtStock;
import itu.greenField.model.MvtStockFille;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.TypeMvt;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MvtStockService {
    private final MvtStockRepository mvtStockRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;

    @Transactional
    public MvtStock saveMvtStock(Commandes commande) {
        MvtStock mvtStock = new MvtStock();
        LocalDateTime currentTimestamp = LocalDateTime.now();
        mvtStock.setDateMvt(currentTimestamp);
        mvtStock.setTypeMouvement(TypeMvt.Vente_Client);
        mvtStock.setPointDeVente(commande.getPointDeVenteRetrait());
        mvtStock =  mvtStockRepository.save(mvtStock);

        for (DetailsCommande detail : commande.getDetailsCommande()) {
            MvtStockFille mvtStockFille = new MvtStockFille();
            mvtStockFille.setMvtStock(mvtStock);
            mvtStockFille.setProduit(detail.getProduit());
            mvtStockFille.setQuantite(detail.getQuantite());
            mvtStockFilleRepository.save(mvtStockFille);
        }
        
        return mvtStock;
    }
}
