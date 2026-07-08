package itu.greenField.service;

import itu.greenField.model.*;
import itu.greenField.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProduitBackService {

    /** En-têtes du fichier Excel produit (export / modèle / import). */
    public static final String[] EXCEL_HEADERS = {
            "Matricule", "Nom", "Categorie", "Prix_Unitaire", "Poids"
    };

    private final ProduitRepository produitRepository;
    private final CategorieProduitRepository categorieProduitRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final MvtStockRepository mvtStockRepository;
    private final MvtStockFilleRepository mvtStockFilleRepository;
    private final EntityExcelService excelService;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public List<Produit> search(Integer idCategorie, String motCle) {
        String cleanMotCle = (motCle != null && !motCle.trim().isEmpty()) ? motCle.trim() : null;
        String pattern = null;
        if (cleanMotCle != null) {
            pattern = "%" + cleanMotCle.toLowerCase() + "%";
        }
        return produitRepository.searchProduits(
                (idCategorie != null && idCategorie > 0) ? idCategorie : null,
                cleanMotCle,
                pattern);
    }

    public Page<Produit> searchPage(Integer idCategorie, String motCle, int page, int size) {
        String cleanMotCle = (motCle != null && !motCle.trim().isEmpty()) ? motCle.trim() : null;
        String pattern = cleanMotCle != null ? "%" + cleanMotCle.toLowerCase() + "%" : null;
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 10 : size, Sort.by("id").descending());
        return produitRepository.searchProduits(
                (idCategorie != null && idCategorie > 0) ? idCategorie : null,
                cleanMotCle,
                pattern,
                pageable);
    }

    // ==================== EXCEL : export / modèle / import ====================

    public byte[] exportExcel() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Produit p : produitRepository.findAll()) {
            rows.add(new Object[] {
                    p.getMatricule(),
                    p.getNom(),
                    p.getCategorie() != null ? p.getCategorie().getLibelle() : "",
                    p.getPu(),
                    p.getPoids()
            });
        }
        return excelService.export("produits", EXCEL_HEADERS, rows);
    }

    public byte[] templateExcel() throws Exception {
        String[] categories = categorieProduitRepository.findAll().stream()
                .map(CategorieProduit::getLibelle)
                .toArray(String[]::new);
        List<EntityExcelService.Dropdown> dropdowns = List.of(
                new EntityExcelService.Dropdown(2, categories) // colonne "Categorie"
        );
        return excelService.template("produits", EXCEL_HEADERS, dropdowns);
    }

    @Transactional
    public int importExcel(InputStream inputStream) throws Exception {
        List<String[]> rows = excelService.read(inputStream, EXCEL_HEADERS.length);
        List<CategorieProduit> categories = categorieProduitRepository.findAll();
        int count = 0;

        for (String[] r : rows) {
            String matricule = r[0].trim();
            String nom = r[1].trim();
            String catLibelle = r[2].trim();
            if (matricule.isEmpty() || nom.isEmpty()) {
                continue;
            }

            CategorieProduit categorie = categories.stream()
                    .filter(c -> c.getLibelle().equalsIgnoreCase(catLibelle))
                    .findFirst()
                    .orElse(null);
            if (categorie == null) {
                continue; // catégorie inconnue : ligne ignorée
            }

            BigDecimal pu;
            try {
                pu = new BigDecimal(r[3].trim().replace(",", "."));
            } catch (Exception e) {
                continue;
            }
            if (pu.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal poids;
            try {
                poids = new BigDecimal(r[4].trim().replace(",", "."));
            } catch (Exception e) {
                poids = BigDecimal.ZERO;
            }

            // upsert par matricule
            Produit produit = produitRepository.findByMatricule(matricule).orElseGet(Produit::new);
            produit.setMatricule(matricule);
            produit.setNom(nom);
            produit.setCategorie(categorie);
            produit.setPu(pu);
            produit.setPoids(poids);
            produitRepository.save(produit);
            count++;
        }
        return count;
    }

    public Optional<Produit> findById(Integer id) {
        return produitRepository.findById(id);
    }

    public List<CategorieProduit> findAllCategories() {
        return categorieProduitRepository.findAll();
    }

    public List<PointDeVente> findAllPointsDeVente() {
        return pointDeVenteRepository.findAll();
    }

    public Map<String, String> validateProduit(Produit produit) {
        Map<String, String> errors = new HashMap<>();

        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            errors.put("nom", "Le nom du produit est obligatoire.");
        }

        if (produit.getMatricule() == null || produit.getMatricule().trim().isEmpty()) {
            errors.put("matricule", "Le matricule est obligatoire.");
        } else {
            boolean exists = (produit.getId() == null)
                    ? produitRepository.existsByMatricule(produit.getMatricule().trim())
                    : produitRepository.existsByMatriculeAndIdNot(produit.getMatricule().trim(), produit.getId());
            if (exists) {
                errors.put("matricule", "Ce matricule est déjà attribué.");
            }
        }

        if (produit.getPu() == null) {
            errors.put("pu", "Le prix unitaire est obligatoire.");
        } else if (produit.getPu().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("pu", "Le prix doit être strictement positif.");
        }

        if (produit.getCategorie() == null || produit.getCategorie().getId() == null) {
            errors.put("categorie", "La catégorie est obligatoire.");
        }

        return errors;
    }

    @Transactional
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    /** Sauvegarde le produit et, si une image est fournie, l'enregistre dans la table photo. */
    @Transactional
    public Produit saveWithImage(Produit produit, org.springframework.web.multipart.MultipartFile image) {
        if (produit.getPoids() == null) {
            produit.setPoids(BigDecimal.ZERO);
        }
        Produit saved = produitRepository.save(produit);
        if (image != null && !image.isEmpty()) {
            String path = fileStorageService.store(image, "produits", "p" + saved.getId());
            photoRepository.save(new Photo(saved, path));
        }
        return saved;
    }

    public List<Photo> getPhotos(Integer idProduit) {
        return photoRepository.findByProduit_Id(idProduit);
    }

    @Transactional
    public void delete(Integer id) {
        produitRepository.deleteById(id);
    }

    public Integer getStock(Integer idProduit, String ptDeVenteCode) {
        String code = (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) ? ptDeVenteCode.trim() : null;
        return mvtStockRepository.getStockByProduitAndOptionalPointDeVente(idProduit, code);
    }

    public List<ProduitStock> getStockLevels(Integer idCategorie, String motCle, String ptDeVenteCode) {
        List<Produit> produits = search(idCategorie, motCle);
        List<ProduitStock> stockList = new ArrayList<>();

        if (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) {
            Optional<PointDeVente> pdvOpt = pointDeVenteRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(ptDeVenteCode))
                    .findFirst();
            String pdvNom = pdvOpt.map(PointDeVente::getNom).orElse("Inconnu");

            for (Produit p : produits) {
                Integer qty = getStock(p.getId(), ptDeVenteCode);
                stockList.add(new ProduitStock(p, ptDeVenteCode, pdvNom, qty));
            }
        } else {
            for (Produit p : produits) {
                Integer qty = getStock(p.getId(), null);
                stockList.add(new ProduitStock(p, null, "Tous les points de vente", qty));
            }
        }

        return stockList;
    }

    @Transactional
    public void addStock(Integer idProduit, String ptDeVenteCode, TypeMvt typeMvt, Integer quantite) {
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive.");
        }

        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));

        PointDeVente pointDeVente = null;
        if (ptDeVenteCode != null && !ptDeVenteCode.trim().isEmpty()) {
            pointDeVente = pointDeVenteRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(ptDeVenteCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Point de vente introuvable."));
        }

        MvtStock mvt = new MvtStock();
        mvt.setTypeMouvement(typeMvt);
        mvt.setPointDeVente(pointDeVente);
        mvtStockRepository.save(mvt);

        MvtStockFille mvtFille = new MvtStockFille();
        mvtFille.setMvtStock(mvt);
        mvtFille.setProduit(produit);
        mvtFille.setQuantite(quantite);
        mvtStockFilleRepository.save(mvtFille);
    }
}
