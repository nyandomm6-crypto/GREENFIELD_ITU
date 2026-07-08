package itu.greenfield.controller;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;
import itu.greenfield.model.Commandes;
import itu.greenfield.model.Tresorerie;
import itu.greenfield.repository.CommandesRepository;
import itu.greenfield.repository.TresorerieRepository;
import itu.greenfield.service.ProduitService;
import itu.greenfield.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/back/statistiques")
@CrossOrigin(origins = "*")
public class BackStatistiqueController {

    @Autowired
    private StatistiqueService statistiqueService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private TresorerieRepository tresorerieRepository;

    @Autowired
    private CommandesRepository commandesRepository;

    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }

    @PostMapping("/tresorerie")
    public ResponseEntity<Tresorerie> createTresorerie(@RequestBody Map<String, Object> payload) {
        Tresorerie tresorerie = new Tresorerie();

        String typeMouvementStr = (String) payload.get("typeMouvement");
        tresorerie.setTypeMouvement(itu.greenfield.model.TypeFlux.valueOf(typeMouvementStr));

        Number montantNum = (Number) payload.get("montant");
        tresorerie.setMontant(new java.math.BigDecimal(montantNum.toString()));

        String dateStr = (String) payload.get("dateOperation");
        if (dateStr != null && !dateStr.isEmpty()) {
            if (dateStr.contains("T")) {
                tresorerie.setDateOperation(java.time.LocalDateTime.parse(dateStr));
            } else {
                tresorerie.setDateOperation(java.time.LocalDate.parse(dateStr).atStartOfDay());
            }
        } else {
            tresorerie.setDateOperation(java.time.LocalDateTime.now());
        }

        tresorerie.setDescription((String) payload.get("description"));

        if (payload.get("idcommande") != null && !payload.get("idcommande").toString().isEmpty()) {
            Integer idcommande = Integer.valueOf(payload.get("idcommande").toString());
            Commandes cmd = commandesRepository.findById(idcommande).orElse(null);
            tresorerie.setCommande(cmd);
        }

        Tresorerie saved = tresorerieRepository.save(tresorerie);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/benefice-fromage")
    public ResponseEntity<Map<String, Object>> getBeneficeFromage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {
        return ResponseEntity.ok(statistiqueService.getTresorerieStats(year, dateDebut, dateFin));
    }

    @GetMapping("/evolution-ventes")
    public ResponseEntity<List<EvolutionVenteDto>> getEvolutionVentes(
            @RequestParam(required = false) Integer idproduit,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statistiqueService.getEvolutionVentes(idproduit, year));
    }

    @GetMapping("/top-clients")
    public ResponseEntity<List<ClientStatDto>> getTop5Clients(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statistiqueService.getTop5Clients(year));
    }

    @GetMapping("/top-produits")
    public ResponseEntity<List<ProduitStatDto>> getTop5Produits(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statistiqueService.getTop5Produits(year));
    }

    @GetMapping("/historique-ventes")
    public ResponseEntity<List<ProduitStatDto>> getHistoriqueVentesGlobal(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statistiqueService.getHistoriqueVentesGlobal(year));
    }
}