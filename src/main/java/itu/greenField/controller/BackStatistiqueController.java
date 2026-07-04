package itu.greenField.controller;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/back/statistiques")
@CrossOrigin(origins = "*")
public class BackStatistiqueController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping("/benefice-fromage")
    public ResponseEntity<Map<String, Object>> getBeneficeFromage() {
        Map<String, Object> response = new HashMap<>();
        response.put("categorie", "Fromage");
        response.put("chiffreAffaires", statistiqueService.getBeneficeFromage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/evolution-ventes")
    public ResponseEntity<List<EvolutionVenteDto>> getEvolutionVentes() {
        return ResponseEntity.ok(statistiqueService.getEvolutionVentes());
    }

    @GetMapping("/top-clients")
    public ResponseEntity<List<ClientStatDto>> getTop5Clients() {
        return ResponseEntity.ok(statistiqueService.getTop5Clients());
    }

    @GetMapping("/historique-ventes")
    public ResponseEntity<List<ProduitStatDto>> getHistoriqueVentesGlobal() {
        // Réutilise la logique du top 5 mais peut être étendu si tu veux tout l'historique
        return ResponseEntity.ok(statistiqueService.getTop5Produits());
    }
}