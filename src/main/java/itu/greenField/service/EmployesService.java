package itu.greenField.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.PointDeVente;
import itu.greenField.repository.EmployesRepository;
import itu.greenField.repository.PointDeVenteRepository;

@Service
public class EmployesService {

    /** En-têtes du fichier Excel employé (export / modèle / import). */
    public static final String[] EXCEL_HEADERS = {
            "Nom", "Prenom", "Adresse", "Contact", "Mail", "MotDePasse", "Role", "Code_Point_De_Vente", "Date"
    };

    private final EmployesRepository employesRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityExcelService excelService;

    public EmployesService(
            EmployesRepository employesRepository,
            PointDeVenteRepository pointDeVenteRepository,
            JdbcTemplate jdbcTemplate,
            EntityExcelService excelService) {
        this.employesRepository = employesRepository;
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.excelService = excelService;
    }

    public List<Employes> filtrer(Boolean estActif, String motCle, LocalDate date, FRole role) {
        String roleFiltre = role == null ? null : role.name();
        return employesRepository.filtrer(estActif, normaliserMotCle(motCle), date, roleFiltre);
    }

    public Page<Employes> filtrerPage(Boolean estActif, String motCle, LocalDate date, FRole role, int page, int size) {
        String roleFiltre = role == null ? null : role.name();
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 10 : size);
        return employesRepository.filtrer(estActif, normaliserMotCle(motCle), date, roleFiltre, pageable);
    }

    // ==================== EXCEL : export / modèle / import ====================

    public byte[] exportExcel() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Employes e : employesRepository.findAll()) {
            rows.add(new Object[] {
                    e.getNom(),
                    e.getPrenom(),
                    e.getAdresse(),
                    e.getContact(),
                    e.getMail(),
                    "", // le mot de passe n'est jamais exporté
                    e.getRole() != null ? e.getRole().name() : "",
                    e.getPointDeVente() != null ? e.getPointDeVente().getCode() : "",
                    e.getDate() != null ? e.getDate().toString() : ""
            });
        }
        return excelService.export("employes", EXCEL_HEADERS, rows);
    }

    public byte[] templateExcel() throws Exception {
        String[] roles = new String[FRole.values().length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = FRole.values()[i].name();
        }
        String[] codesPdv = pointDeVenteRepository.findAll().stream()
                .map(PointDeVente::getCode)
                .toArray(String[]::new);
        List<EntityExcelService.Dropdown> dropdowns = new ArrayList<>();
        dropdowns.add(new EntityExcelService.Dropdown(6, roles));    // colonne "Role"
        dropdowns.add(new EntityExcelService.Dropdown(7, codesPdv)); // colonne "Code_Point_De_Vente"
        return excelService.template("employes", EXCEL_HEADERS, dropdowns);
    }

    @Transactional
    public int importExcel(InputStream inputStream) throws Exception {
        List<String[]> rows = excelService.read(inputStream, EXCEL_HEADERS.length);
        int count = 0;

        for (String[] r : rows) {
            String nom = r[0].trim();
            String prenom = r[1].trim();
            String mail = r[4].trim();
            String roleStr = r[6].trim();
            if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || roleStr.isEmpty()) {
                continue;
            }

            FRole role;
            try {
                role = FRole.valueOf(roleStr);
            } catch (Exception e) {
                continue; // rôle inconnu : ligne ignorée
            }

            Employes employe = new Employes();
            employe.setNom(nom);
            employe.setPrenom(prenom);
            employe.setAdresse(texteOuNull(r[2]));
            employe.setContact(texteOuNull(r[3]));
            employe.setMail(mail);
            employe.setMotdepasse(r[5].trim());
            employe.setRole(role);

            String code = texteOuNull(r[7]);
            String dateStr = r[8].trim();
            if (dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            employe.setDate(ImportExcelService.localDateValidator(dateStr));

            try {
                Employes existant = employesRepository.findByMailIgnoreCase(mail).orElse(null);
                if (existant != null) {
                    update(existant.getId(), employe, code);
                } else {
                    if (!aDuTexte(employe.getMotdepasse())) {
                        employe.setMotdepasse("greenfield"); // mot de passe par défaut si absent
                    }
                    create(employe, code);
                }
                count++;
            } catch (Exception ex) {
                // ligne invalide : on continue avec les suivantes
            }
        }
        return count;
    }

    public Employes getById(Integer id) {
        return employesRepository.findWithPointDeVenteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employe introuvable."));
    }

    @Transactional
    public Employes create(Employes employe, String codePointDeVente) {
        valider(employe, true);
        verifierMailUnique(employe.getMail(), null);

        String code = normaliserCodePointDeVente(codePointDeVente);
        LocalDate date = employe.getDate() == null ? LocalDate.now() : employe.getDate();

        Integer id = jdbcTemplate.queryForObject("""
                INSERT INTO employes
                    (nom, prenom, adresse, contact, mail, motdepasse, role, idptdevente, est_actif, date)
                VALUES
                    (?, ?, ?, ?, ?, ?, CAST(? AS f_role), ?, ?, ?)
                RETURNING id
                """,
                Integer.class,
                employe.getNom().trim(),
                employe.getPrenom().trim(),
                texteOuNull(employe.getAdresse()),
                texteOuNull(employe.getContact()),
                employe.getMail().trim(),
                employe.getMotdepasse(),
                employe.getRole().name(),
                code,
                true,
                date);

        return getById(id);
    }

    @Transactional
    public Employes update(Integer id, Employes employe, String codePointDeVente) {
        Employes employeActuel = getById(id);
        valider(employe, false);
        verifierMailUnique(employe.getMail(), id);

        String code = normaliserCodePointDeVente(codePointDeVente);
        LocalDate date = employe.getDate() == null ? employeActuel.getDate() : employe.getDate();

        if (aDuTexte(employe.getMotdepasse())) {
            jdbcTemplate.update("""
                    UPDATE employes
                    SET nom = ?,
                        prenom = ?,
                        adresse = ?,
                        contact = ?,
                        mail = ?,
                        motdepasse = ?,
                        role = CAST(? AS f_role),
                        idptdevente = ?,
                        date = ?
                    WHERE id = ?
                    """,
                    employe.getNom().trim(),
                    employe.getPrenom().trim(),
                    texteOuNull(employe.getAdresse()),
                    texteOuNull(employe.getContact()),
                    employe.getMail().trim(),
                    employe.getMotdepasse(),
                    employe.getRole().name(),
                    code,
                    date,
                    id);
        } else {
            jdbcTemplate.update("""
                    UPDATE employes
                    SET nom = ?,
                        prenom = ?,
                        adresse = ?,
                        contact = ?,
                        mail = ?,
                        role = CAST(? AS f_role),
                        idptdevente = ?,
                        date = ?
                    WHERE id = ?
                    """,
                    employe.getNom().trim(),
                    employe.getPrenom().trim(),
                    texteOuNull(employe.getAdresse()),
                    texteOuNull(employe.getContact()),
                    employe.getMail().trim(),
                    employe.getRole().name(),
                    code,
                    date,
                    id);
        }

        return getById(id);
    }

    @Transactional
    public void delete(Integer id) {
        getById(id);
        jdbcTemplate.update("UPDATE employes SET est_actif = false WHERE id = ?", id);
    }

    private void valider(Employes employe, boolean creation) {
        if (!aDuTexte(employe.getNom())) {
            throw new IllegalArgumentException("Le nom est obligatoire.");
        }
        if (!aDuTexte(employe.getPrenom())) {
            throw new IllegalArgumentException("Le prenom est obligatoire.");
        }
        if (!aDuTexte(employe.getMail()) || !employe.getMail().contains("@")) {
            throw new IllegalArgumentException("Le mail est invalide.");
        }
        if (creation && !aDuTexte(employe.getMotdepasse())) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }
        if (employe.getRole() == null) {
            throw new IllegalArgumentException("Le role est obligatoire.");
        }
    }

    private void verifierMailUnique(String mail, Integer idActuel) {
        employesRepository.findByMailIgnoreCase(mail.trim())
                .filter(e -> idActuel == null || !e.getId().equals(idActuel))
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Ce mail est deja utilise.");
                });
    }

    private String normaliserCodePointDeVente(String codePointDeVente) {
        if (!aDuTexte(codePointDeVente)) {
            return null;
        }

        String code = codePointDeVente.trim();
        PointDeVente pointDeVente = pointDeVenteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Point de vente introuvable."));
        return pointDeVente.getCode();
    }

    private String normaliserMotCle(String motCle) {
        return aDuTexte(motCle) ? motCle.trim() : null;
    }

    private String texteOuNull(String valeur) {
        return aDuTexte(valeur) ? valeur.trim() : null;
    }

    private boolean aDuTexte(String valeur) {
        return valeur != null && !valeur.trim().isEmpty();
    }

    public List<Employes> getLivreur() {
        return employesRepository.findAll();
    }

    public Employes findByEmail(String email) {
        return employesRepository.findByMail(email).orElse(null);
    }
}
