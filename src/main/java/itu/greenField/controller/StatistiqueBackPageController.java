package itu.greenField.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import itu.greenField.model.TypeFlux;
import itu.greenField.repository.CategorieProduitRepository;
import itu.greenField.repository.ProduitRepository;
import itu.greenField.repository.StatistiqueRepository;
import itu.greenField.repository.TresorerieRepository;
import lombok.RequiredArgsConstructor;

/**
 * Statistiques back-office : page de tableaux de bord + endpoints JSON
 * consommés par Chart.js (camembert recettes/dépenses, courbe des ventes,
 * histogramme top 5).
 */
@Controller
@RequestMapping("/back/statistiques")
@RequiredArgsConstructor
public class StatistiqueBackPageController {

    private static final String[] MOIS = {
            "Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"
    };

    private final StatistiqueRepository statistiqueRepository;
    private final TresorerieRepository tresorerieRepository;
    private final CategorieProduitRepository categorieRepository;
    private final ProduitRepository produitRepository;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        List<Integer> annees = statistiqueRepository.anneesDisponibles();
        model.addAttribute("annees", annees);
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("produits", produitRepository.findAll());
        return "back/statistiques/dashboard";
    }

    // ===== Camembert : recettes (ventes) vs dépenses, filtre période =====
    @GetMapping("/api/tresorerie")
    @ResponseBody
    public Map<String, Object> tresorerie(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        // Bornes toujours non-null (évite les soucis de type null côté PostgreSQL)
        LocalDateTime debutDt = debut != null ? debut.atStartOfDay() : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime finDt = fin != null ? fin.atTime(23, 59, 59) : LocalDateTime.of(2999, 12, 31, 23, 59, 59);

        BigDecimal recettes = tresorerieRepository.sommeParType(TypeFlux.Entree_Vente, debutDt, finDt);
        BigDecimal depenses = tresorerieRepository.sommeParType(TypeFlux.Depense_Exploitation, debutDt, finDt);
        if (recettes == null) {
            recettes = BigDecimal.ZERO;
        }
        if (depenses == null) {
            depenses = BigDecimal.ZERO;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("recettes", recettes);
        response.put("depenses", depenses);
        response.put("benefice", recettes.subtract(depenses));
        return response;
    }

    // ===== Courbe : ventes mensuelles d'un produit sur une année =====
    @GetMapping("/api/evolution")
    @ResponseBody
    public Map<String, Object> evolution(@RequestParam Integer idProduit, @RequestParam Integer annee) {
        long[] parMois = new long[12];
        for (Object[] row : statistiqueRepository.ventesMensuellesParProduit(idProduit, annee)) {
            int mois = ((Number) row[0]).intValue(); // 1..12
            long qte = ((Number) row[1]).longValue();
            if (mois >= 1 && mois <= 12) {
                parMois[mois - 1] = qte;
            }
        }
        List<Long> data = new ArrayList<>();
        for (long v : parMois) {
            data.add(v);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", MOIS);
        response.put("data", data);
        return response;
    }

    // ===== Histogramme : top 5 produits sur une année (option catégorie) =====
    @GetMapping("/api/top5")
    @ResponseBody
    public Map<String, Object> top5(@RequestParam Integer annee,
            @RequestParam(required = false) Integer idCategorie) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (Object[] row : statistiqueRepository.top5ParAnneeEtCategorie(annee, idCategorie)) {
            labels.add((String) row[0]);
            data.add(((Number) row[1]).longValue());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);
        return response;
    }
}
