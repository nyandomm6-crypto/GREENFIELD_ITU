package itu.greenfield;

import itu.greenfield.repository.StatistiqueRepository;
import itu.greenfield.repository.TresorerieRepository;
import itu.greenfield.model.Tresorerie;
import itu.greenfield.model.TypeFlux;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
class StatistiqueRepositoryTest {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Autowired
    private TresorerieRepository tresorerieRepository;

    @Test
    void testHistoriqueVentes() {
        statistiqueRepository.findHistoriqueVentesGlobal(
                java.sql.Timestamp.valueOf("1970-01-01 00:00:00"),
                java.sql.Timestamp.valueOf("2099-12-31 23:59:59"));
    }

    @Test
    void testTop5Produits() {
        statistiqueRepository.findTop5ProduitsPlusVendus(
                java.sql.Timestamp.valueOf("1970-01-01 00:00:00"),
                java.sql.Timestamp.valueOf("2099-12-31 23:59:59"));
    }

    @Test
    void testBeneficeFromage() {
        statistiqueRepository.getChiffreAffairesParCategorie(
                "fromage",
                java.sql.Timestamp.valueOf("1970-01-01 00:00:00"),
                java.sql.Timestamp.valueOf("2099-12-31 23:59:59"));
    }

    @Test
    void testEvolutionVentes() {
        statistiqueRepository.findEvolutionDesVentes(
                null,
                java.sql.Timestamp.valueOf("1970-01-01 00:00:00"),
                java.sql.Timestamp.valueOf("2099-12-31 23:59:59"));
    }

    @Test
    void testTopClients() {
        statistiqueRepository.findTop5MeilleursClients(
                java.sql.Timestamp.valueOf("1970-01-01 00:00:00"),
                java.sql.Timestamp.valueOf("2099-12-31 23:59:59"));
    }

    @Test
    void testSaveTresorerie() {
        Tresorerie t = new Tresorerie();
        t.setTypeMouvement(TypeFlux.Depense_Exploitation);
        t.setMontant(new BigDecimal("15000"));
        t.setDateOperation(LocalDateTime.now());
        t.setDescription("test");
        tresorerieRepository.save(t);
    }
}
