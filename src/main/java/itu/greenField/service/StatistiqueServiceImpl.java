package itu.greenField.service;

import itu.greenField.dto.ClientStatDto;
import itu.greenField.dto.EvolutionVenteDto;
import itu.greenField.dto.ProduitStatDto;
import itu.greenField.model.Produit;
import itu.greenField.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {

    private static final Timestamp MIN_TIMESTAMP = Timestamp.valueOf("1970-01-01 00:00:00");
    private static final Timestamp MAX_TIMESTAMP = Timestamp.valueOf("2099-12-31 23:59:59");
    private static final LocalDateTime MIN_LOCAL = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime MAX_LOCAL = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Override
    public List<ProduitStatDto> getTop5Produits(Integer year) {
        Timestamp[] range = resolveTimestampRange(year, null, null);
        return statistiqueRepository.findTop5ProduitsPlusVendus(range[0], range[1]);
    }

    @Override
    public List<Produit> getNouveauxProduits() {
        return statistiqueRepository.findNouveauxProduits();
    }

    @Override
    public Map<String, Object> getTresorerieStats(Integer year, String dateDebutStr, String dateFinStr) {
        Timestamp[] tsRange = resolveTimestampRange(year, dateDebutStr, dateFinStr);
        LocalDateTime[] ldtRange = resolveLocalDateTimeRange(year, dateDebutStr, dateFinStr);

        Double caFromage = statistiqueRepository.getChiffreAffairesParCategorie("fromage", tsRange[0], tsRange[1]);
        Double totalEntrees = statistiqueRepository.getSommeTresorerie(
                itu.greenfield.model.TypeFlux.Entree_Vente, ldtRange[0], ldtRange[1]);
        Double totalDepenses = statistiqueRepository.getSommeTresorerie(
                itu.greenfield.model.TypeFlux.Depense_Exploitation, ldtRange[0], ldtRange[1]);
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
        Timestamp[] range = resolveTimestampRange(year, null, null);
        return statistiqueRepository.findEvolutionDesVentes(idproduit, range[0], range[1]);
    }

    @Override
    public List<ClientStatDto> getTop5Clients(Integer year) {
        Timestamp[] range = resolveTimestampRange(year, null, null);
        return statistiqueRepository.findTop5MeilleursClients(range[0], range[1]);
    }

    @Override
    public List<ProduitStatDto> getHistoriqueVentesGlobal(Integer year) {
        Timestamp[] range = resolveTimestampRange(year, null, null);
        return statistiqueRepository.findHistoriqueVentesGlobal(range[0], range[1]);
    }

    private Timestamp[] resolveTimestampRange(Integer year, String dateDebutStr, String dateFinStr) {
        Timestamp dateDebut = parseTimestamp(dateDebutStr, false);
        Timestamp dateFin = parseTimestamp(dateFinStr, true);
        if (dateDebut == null && dateFin == null && year != null) {
            dateDebut = Timestamp.valueOf(LocalDateTime.of(year, 1, 1, 0, 0, 0));
            dateFin = Timestamp.valueOf(LocalDateTime.of(year, 12, 31, 23, 59, 59));
        }
        if (dateDebut == null) {
            dateDebut = MIN_TIMESTAMP;
        }
        if (dateFin == null) {
            dateFin = MAX_TIMESTAMP;
        }
        return new Timestamp[] { dateDebut, dateFin };
    }

    private LocalDateTime[] resolveLocalDateTimeRange(Integer year, String dateDebutStr, String dateFinStr) {
        LocalDateTime dateDebut = parseLocalDateTime(dateDebutStr, false);
        LocalDateTime dateFin = parseLocalDateTime(dateFinStr, true);
        if (dateDebut == null && dateFin == null && year != null) {
            dateDebut = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            dateFin = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        }
        if (dateDebut == null) {
            dateDebut = MIN_LOCAL;
        }
        if (dateFin == null) {
            dateFin = MAX_LOCAL;
        }
        return new LocalDateTime[] { dateDebut, dateFin };
    }

    private Timestamp parseTimestamp(String dateStr, boolean isEnd) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            if (dateStr.contains("T")) {
                return Timestamp.valueOf(LocalDateTime.parse(dateStr));
            }
            LocalDate ld = LocalDate.parse(dateStr);
            LocalDateTime ldt = isEnd ? ld.atTime(23, 59, 59, 999999999) : ld.atStartOfDay();
            return Timestamp.valueOf(ldt);
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
            }
            LocalDate ld = LocalDate.parse(dateStr);
            return isEnd ? ld.atTime(23, 59, 59, 999999999) : ld.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }
}
