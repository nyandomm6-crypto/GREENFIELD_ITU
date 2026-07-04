package itu.greenfield.service;

import itu.greenfield.dto.ClientStatDto;
import itu.greenfield.dto.EvolutionVenteDto;
import itu.greenfield.dto.ProduitStatDto;
import itu.greenfield.model.Produit;
import itu.greenfield.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Override
    public List<ProduitStatDto> getTop5Produits(Integer year) {
        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        if (year != null) {
            dateDebut = Timestamp.valueOf(LocalDateTime.of(year, 1, 1, 0, 0, 0));
            dateFin = Timestamp.valueOf(LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999));
        }
        return statistiqueRepository.findTop5ProduitsPlusVendus(dateDebut, dateFin);
    }

    @Override
    public List<Produit> getNouveauxProduits() {
        return statistiqueRepository.findNouveauxProduits();
    }

    @Override
    public Map<String, Object> getTresorerieStats(Integer year, String dateDebutStr, String dateFinStr) {
        Timestamp dbTimestamp = parseTimestamp(dateDebutStr, false);
        Timestamp dfTimestamp = parseTimestamp(dateFinStr, true);
        if (dbTimestamp == null && dfTimestamp == null && year != null) {
            dbTimestamp = Timestamp.valueOf(LocalDateTime.of(year, 1, 1, 0, 0, 0));
            dfTimestamp = Timestamp.valueOf(LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999));
        }

        LocalDateTime dbLocal = parseLocalDateTime(dateDebutStr, false);
        LocalDateTime dfLocal = parseLocalDateTime(dateFinStr, true);
        if (dbLocal == null && dfLocal == null && year != null) {
            dbLocal = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            dfLocal = LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999);
        }

        Double caFromage = statistiqueRepository.getChiffreAffairesParCategorie("fromage", dbTimestamp, dfTimestamp);
        Double totalEntrees = statistiqueRepository.getSommeTresorerie(itu.greenfield.model.TypeFlux.Entree_Vente, dbLocal, dfLocal);
        Double totalDepenses = statistiqueRepository.getSommeTresorerie(itu.greenfield.model.TypeFlux.Depense_Exploitation, dbLocal, dfLocal);
        Double beneficeGlobal = totalEntrees - totalDepenses;

        Map<String, Object> stats = new HashMap<>();
        stats.put("chiffreAffairesFromage", caFromage);
        stats.put("totalEntrees", totalEntrees);
        stats.put("totalDepenses", totalDepenses);
        stats.put("beneficeGlobal", beneficeGlobal);
        return stats;
     }

    @Override
    public List<EvolutionVenteDto> getEvolutionVentes(Integer idproduit, Integer year) {
        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        if (year != null) {
            dateDebut = Timestamp.valueOf(LocalDateTime.of(year, 1, 1, 0, 0, 0));
            dateFin = Timestamp.valueOf(LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999));
        }
        return statistiqueRepository.findEvolutionDesVentes(idproduit, dateDebut, dateFin);
    }

    @Override
    public List<ClientStatDto> getTop5Clients(Integer year) {
        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        if (year != null) {
            dateDebut = Timestamp.valueOf(LocalDateTime.of(year, 1, 1, 0, 0, 0));
            dateFin = Timestamp.valueOf(LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999));
        }
        return statistiqueRepository.findTop5MeilleursClients(dateDebut, dateFin);
    }

    // Helper methods for date parsing
    private Timestamp parseTimestamp(String dateStr, boolean isEnd) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            if (dateStr.contains("T")) {
                LocalDateTime ldt = LocalDateTime.parse(dateStr);
                return Timestamp.valueOf(ldt);
            } else {
                LocalDate ld = LocalDate.parse(dateStr);
                LocalDateTime ldt = isEnd ? ld.atTime(23, 59, 59, 999999999) : ld.atStartOfDay();
                return Timestamp.valueOf(ldt);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseLocalDateTime(String dateStr, boolean isEnd) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            if (dateStr.contains("T")) {
                return LocalDateTime.parse(dateStr);
            } else {
                LocalDate ld = LocalDate.parse(dateStr);
                return isEnd ? ld.atTime(23, 59, 59, 999999999) : ld.atStartOfDay();
            }
        } catch (Exception e) {
            return null;
        }
    }
}