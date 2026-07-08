package itu.greenField.service;

import itu.greenField.model.Vehicule;
import itu.greenField.model.StatutVehicule;
import itu.greenField.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculeService {

    /** En-têtes du fichier Excel véhicule (export / modèle / import). */
    public static final String[] EXCEL_HEADERS = {
            "Matricule", "Marque", "Modele", "Annee", "Capacite", "Statut", "Date"
    };

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private EntityExcelService excelService;

    // II-Liste véhicule (avec recherche)
    public List<Vehicule> listerEtFiltrer(String motCle, LocalDate date, StatutVehicule statut) {
        if (motCle != null || date != null || statut != null) {
            return vehiculeRepository.filtrerVehicules(motCle, date, statut);
        }
        return vehiculeRepository.findAll();
    }

    public Page<Vehicule> listerPage(String motCle, LocalDate date, StatutVehicule statut, int page, int size) {
        String cleanMotCle = (motCle != null && !motCle.trim().isEmpty()) ? motCle.trim().toLowerCase() : "";
        String pattern = "%" + cleanMotCle + "%";
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 10 : size, Sort.by("id").descending());
        return vehiculeRepository.rechercheVehicules(pattern, date, statut, pageable);
    }

    // ==================== EXCEL : export / modèle / import ====================

    public byte[] exportExcel() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Vehicule v : vehiculeRepository.findAll()) {
            rows.add(new Object[] {
                    v.getMatricule(),
                    v.getMarque(),
                    v.getModele(),
                    v.getAnnee(),
                    v.getCapacite(),
                    v.getStatut() != null ? v.getStatut().name() : "",
                    v.getDate() != null ? v.getDate().toString() : ""
            });
        }
        return excelService.export("vehicules", EXCEL_HEADERS, rows);
    }

    public byte[] templateExcel() throws Exception {
        String[] statuts = new String[StatutVehicule.values().length];
        for (int i = 0; i < statuts.length; i++) {
            statuts[i] = StatutVehicule.values()[i].name();
        }
        List<EntityExcelService.Dropdown> dropdowns = List.of(
                new EntityExcelService.Dropdown(5, statuts) // colonne "Statut"
        );
        return excelService.template("vehicules", EXCEL_HEADERS, dropdowns);
    }

    @Transactional
    public int importExcel(InputStream inputStream) throws Exception {
        List<String[]> rows = excelService.read(inputStream, EXCEL_HEADERS.length);
        int count = 0;

        for (String[] r : rows) {
            String matricule = r[0].trim();
            String marque = r[1].trim();
            String modele = r[2].trim();
            if (matricule.isEmpty() || marque.isEmpty() || modele.isEmpty()) {
                continue;
            }

            Vehicule vehicule = vehiculeRepository.findByMatricule(matricule).orElseGet(Vehicule::new);
            vehicule.setMatricule(matricule);
            vehicule.setMarque(marque);
            vehicule.setModele(modele);

            try {
                if (!r[3].trim().isEmpty()) {
                    vehicule.setAnnee((int) Double.parseDouble(r[3].trim()));
                }
            } catch (Exception ignore) {
            }
            try {
                if (!r[4].trim().isEmpty()) {
                    vehicule.setCapacite(new BigDecimal(r[4].trim().replace(",", ".")));
                }
            } catch (Exception ignore) {
            }
            try {
                if (!r[5].trim().isEmpty()) {
                    vehicule.setStatut(StatutVehicule.valueOf(r[5].trim()));
                }
            } catch (Exception ignore) {
            }

            String dateStr = r[6].trim();
            if (dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            LocalDate date = ImportExcelService.localDateValidator(dateStr);
            vehicule.setDate(date != null ? date : LocalDate.now());

            vehiculeRepository.save(vehicule);
            count++;
        }
        return count;
    }

    // I.A - Voir détails (Get by ID)
    public Optional<Vehicule> obtenirParId(Integer id) {
        return vehiculeRepository.findById(id);
    }

    // I.D - Ajouter véhicule (Create)
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        if (vehicule.getDate() == null) {
            vehicule.setDate(LocalDate.now()); // Date du jour par défaut
        }
        return vehiculeRepository.save(vehicule);
    }

    // I.C - Modifier véhicule (Update)
    public Vehicule modifierVehicule(Integer id, Vehicule nouveauxDetails) {
        return vehiculeRepository.findById(id).map(vehicule -> {
            vehicule.setMatricule(nouveauxDetails.getMatricule());
            vehicule.setMarque(nouveauxDetails.getMarque());
            vehicule.setModele(nouveauxDetails.getModele());
            vehicule.setAnnee(nouveauxDetails.getAnnee());
            vehicule.setCapacite(nouveauxDetails.getCapacite());
            vehicule.setStatut(nouveauxDetails.getStatut());
            if (nouveauxDetails.getDate() != null) {
                vehicule.setDate(nouveauxDetails.getDate());
            }
            return vehiculeRepository.save(vehicule);
        }).orElseThrow(() -> new RuntimeException("Véhicule introuvable avec l'id : " + id));
    }

    // I.B - Supprimer
    public void supprimerVehicule(Integer id) {
        vehiculeRepository.deleteById(id);
    }

    public List<Vehicule> getVehicule() {
        return vehiculeRepository.findAll();
    }
}