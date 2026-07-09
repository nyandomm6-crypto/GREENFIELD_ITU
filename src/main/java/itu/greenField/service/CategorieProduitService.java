package itu.greenField.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.CategorieProduit;
import itu.greenField.repository.CategorieProduitRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieProduitService {

    /** En-têtes du fichier Excel catégorie (export / modèle / import). */
    public static final String[] EXCEL_HEADERS = { "Libelle" };

    private final CategorieProduitRepository categorieRepository;
    private final EntityExcelService excelService;

    public Page<CategorieProduit> searchPage(String motCle, int page, int size) {
        String cleanMotCle = (motCle != null && !motCle.trim().isEmpty()) ? motCle.trim() : null;
        String pattern = cleanMotCle != null ? "%" + cleanMotCle.toLowerCase() + "%" : null;
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 10 : size, Sort.by("id").descending());
        return categorieRepository.search(cleanMotCle, pattern, pageable);
    }

    public Optional<CategorieProduit> findById(Integer id) {
        return categorieRepository.findById(id);
    }

    public String validate(CategorieProduit categorie) {
        if (categorie.getLibelle() == null || categorie.getLibelle().trim().isEmpty()) {
            return "Le libellé est obligatoire.";
        }
        Optional<CategorieProduit> existant = categorieRepository
                .findFirstByLibelleIgnoreCase(categorie.getLibelle().trim());
        if (existant.isPresent() && !existant.get().getId().equals(categorie.getId())) {
            return "Ce libellé de catégorie existe déjà.";
        }
        return null;
    }

    @Transactional
    public CategorieProduit save(CategorieProduit categorie) {
        categorie.setLibelle(categorie.getLibelle().trim());
        return categorieRepository.save(categorie);
    }

    @Transactional
    public void delete(Integer id) {
        categorieRepository.deleteById(id);
    }

    // ==================== EXCEL : export / modèle / import ====================

    public byte[] exportExcel() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (CategorieProduit c : categorieRepository.findAll()) {
            rows.add(new Object[] { c.getLibelle() });
        }
        return excelService.export("categories", EXCEL_HEADERS, rows);
    }

    public byte[] templateExcel() throws Exception {
        return excelService.template("categories", EXCEL_HEADERS, null);
    }

    @Transactional
    public int importExcel(InputStream inputStream) throws Exception {
        List<String[]> rows = excelService.read(inputStream, EXCEL_HEADERS.length);
        int count = 0;
        for (String[] r : rows) {
            String libelle = r[0].trim();
            if (libelle.isEmpty()) {
                continue;
            }
            // upsert par libellé (insensible à la casse)
            CategorieProduit categorie = categorieRepository.findFirstByLibelleIgnoreCase(libelle)
                    .orElseGet(CategorieProduit::new);
            categorie.setLibelle(libelle);
            categorieRepository.save(categorie);
            count++;
        }
        return count;
    }

    public List<CategorieProduit> findAll() {
        return categorieRepository.findAll();
    }
}
