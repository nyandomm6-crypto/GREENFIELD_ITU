package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itu.GreenField.model.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    public Client findByMail(String email);

    @Query(value = "SELECT * FROM client c " +
            "WHERE c.nom ILIKE CONCAT('%', :nom, '%') " +
            "OR c.prenom ILIKE CONCAT('%', :prenom, '%')", nativeQuery = true)
    List<Client> findClientsByNometPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
}
