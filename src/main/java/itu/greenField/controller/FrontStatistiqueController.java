package itu.greenField.controller;

import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;
import itu.greenField.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/front/statistiques")
@CrossOrigin(origins = "*") // À adapter selon ton URL Front
public class FrontStatistiqueController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping("/top-produits")
    public ResponseEntity<List<ProduitStatDto>> getTop5Produits() {
        return ResponseEntity.ok(statistiqueService.getTop5Produits());
    }

    @GetMapping("/nouveaux-produits")
    public ResponseEntity<List<Produit>> getNouveauxProduits() {
        return ResponseEntity.ok(statistiqueService.getNouveauxProduits());
    }
}