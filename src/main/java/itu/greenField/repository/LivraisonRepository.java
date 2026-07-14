package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.Livraison;

public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    public Livraison getById(Integer id);

    List<Livraison> findByLivreur(Employes livreur);

    @Query(value = "SELECT * FROM livraison l WHERE l.idlivreur = :id and l.statutLivraison = 'En_attente'", nativeQuery = true)
    public List<Livraison> findByLivreurDispo(@Param("id") Integer id);
}
