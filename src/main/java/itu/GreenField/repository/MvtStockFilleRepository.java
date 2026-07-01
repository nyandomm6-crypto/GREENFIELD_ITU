package itu.GreenField.repository;

import itu.GreenField.model.MvtStockFille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MvtStockFilleRepository extends JpaRepository<MvtStockFille, Integer> {
    // Vous pouvez ajouter des méthodes personnalisées ici si nécessaire
}
