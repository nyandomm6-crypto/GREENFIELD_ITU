package itu.greenField.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.FRole;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.Employes;

public interface EmployesRepository extends JpaRepository<Employes, Integer> {
        Optional<Employes> findByMailIgnoreCase(String mail);

        @Query("""
                        SELECT e
                        FROM Employes e
                        LEFT JOIN FETCH e.pointDeVente
                        WHERE e.id = :id
                        """)
        Optional<Employes> findWithPointDeVenteById(@Param("id") Integer id);

        @Query(value = """
                        SELECT e.*
                        FROM employes e
                        LEFT JOIN pointdevente p ON p.code = e.idptdevente
                        WHERE COALESCE(e.est_actif, true) = :estActif
                          AND (CAST(:date AS date) IS NULL OR e.date = CAST(:date AS date))
                          AND (
                                CAST(:motCle AS text) IS NULL
                                OR COALESCE(e.nom, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                                OR COALESCE(e.prenom, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                                OR COALESCE(e.mail, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                                OR COALESCE(e.contact, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                                OR COALESCE(p.nom, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                                OR COALESCE(p.code, '') ILIKE CONCAT('%', CAST(:motCle AS text), '%')
                          )
                        ORDER BY e.nom ASC, e.prenom ASC
                        """, nativeQuery = true)
        List<Employes> filtrer(
                        @Param("estActif") Boolean estActif,
                        @Param("motCle") String motCle,
                        @Param("date") LocalDate date,
                        @Param("role") String role);

        public Employes getById(Integer id);

        List<Employes> findByPointDeVente(PointDeVente pointDeVente);

        List<Employes> findByPointDeVenteAndNomContainingIgnoreCase(PointDeVente pointDeVente, String nom);

        List<Employes> findByPointDeVenteAndRole(PointDeVente pointDeVente, FRole role);

        List<Employes> findByPointDeVenteAndNomContainingIgnoreCaseAndRole(PointDeVente pointDeVente, String nom,
                        FRole role);

        Optional<Employes> findByMail(String mail);
}
