package itu.GreenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import itu.GreenField.model.ValidationMail;

@Repository
public interface ValidationMailRepository extends JpaRepository<ValidationMail, Integer> {

    // chercher par token (important pour OTP)
    Optional<ValidationMail> findByToken(String token);

    ValidationMail findByClient_Id(Integer idClient);

    @Query(value = """
            SELECT vm.*
            FROM validation_mail vm
            JOIN client c ON vm.id_client = c.id
            WHERE c.mail = :email
            ORDER BY vm.id DESC
            LIMIT 1
            """, nativeQuery = true)
    ValidationMail findByEmail(@Param("email") String email);
}