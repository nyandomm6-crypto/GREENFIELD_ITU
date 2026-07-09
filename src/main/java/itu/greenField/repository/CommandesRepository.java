package itu.greenField.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.StatutCommande;

public interface CommandesRepository extends JpaRepository<Commandes, Integer> {
    public Commandes getById(Integer id);

    public List<Commandes> findByClient(Client client);

    @Query("SELECT c FROM Commandes c WHERE c.client = :client "
            + "AND (:statut IS NULL OR :statut = '' OR lower(c.statutActuel.nom) = lower(:statut)) "
            + "AND (:motCle IS NULL OR :motCle = '' OR str(c.id) LIKE concat('%', :motCle, '%'))")
    Page<Commandes> findByClientWithFiltre(@Param("client") Client client,
            @Param("motCle") String motCle,
            @Param("statut") String statut,
            Pageable pageable);

    List<Commandes> findByStatutActuel(StatutCommande statutActuel);

    /** Commandes rattachées à un point de vente de retrait donné (par code). */
    List<Commandes> findByPointDeVenteRetrait_CodeOrderByIdDesc(String code);

    @Query(value = "SELECT * FROM commandes c " +
            "WHERE c.statutactuel != 2 " +
            "AND c.statutactuel != 4", nativeQuery = true)
    public List<Commandes> findDispoCommandes();

    @Query(value = "SELECT * FROM commandes c", nativeQuery = true)
    public Page<Commandes> findAllPaginated(Pageable pageable);
}
