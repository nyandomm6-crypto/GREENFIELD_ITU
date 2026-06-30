package itu.GreenField.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.GreenField.model.Employes;
import itu.GreenField.model.FRole;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.repository.EmployesRepository;
import itu.GreenField.repository.PointDeVenteRepository;

@Service
public class EmployesService {
    private final EmployesRepository employesRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final JdbcTemplate jdbcTemplate;

    public EmployesService(
            EmployesRepository employesRepository,
            PointDeVenteRepository pointDeVenteRepository,
            JdbcTemplate jdbcTemplate) {
        this.employesRepository = employesRepository;
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Employes> filtrer(Boolean estActif, String motCle, LocalDate date, FRole role) {
        String roleFiltre = role == null ? null : role.name();
        return employesRepository.filtrer(estActif, normaliserMotCle(motCle), date, roleFiltre);
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
}
