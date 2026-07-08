package itu.greenField.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.*;
import itu.greenField.model.StatutVehicule;
import itu.greenField.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {
        // II-Liste vehicule : Filtre par mot clés, date, status
        @Query("SELECT v FROM Vehicule v WHERE " +
                        "(:motCle IS NULL OR LOWER(v.marque) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
                        "OR LOWER(v.modele) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
                        "OR LOWER(v.matricule) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
                        "AND (:date IS NULL OR v.date = :date) " +
                        "AND (:statut IS NULL OR v.statut = :statut)")
        List<Vehicule> filtrerVehicules(@Param("motCle") String motCle,
                        @Param("date") LocalDate date,
                        @Param("statut") StatutVehicule statut);

        @Query("SELECT v FROM Vehicule v WHERE " +
                        "(LOWER(v.marque) LIKE :pattern " +
                        "OR LOWER(v.modele) LIKE :pattern " +
                        "OR LOWER(v.matricule) LIKE :pattern) " +
                        "AND (:date IS NULL OR v.date = :date) " +
                        "AND (:statut IS NULL OR v.statut = :statut)")
        Page<Vehicule> rechercheVehicules(@Param("pattern") String pattern,
                        @Param("date") LocalDate date,
                        @Param("statut") StatutVehicule statut,
                        Pageable pageable);

        Optional<Vehicule> findByMatricule(String matricule);

        public Vehicule getById(Integer id);

}
