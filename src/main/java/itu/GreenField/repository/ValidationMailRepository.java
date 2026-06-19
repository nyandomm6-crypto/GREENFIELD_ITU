package itu.GreenField.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itu.GreenField.model.ValidationMail;

@Repository
public interface ValidationMailRepository extends JpaRepository<ValidationMail, Integer> {

    // chercher par token (important pour OTP)
    Optional<ValidationMail> findByToken(String token);

    ValidationMail findByClientId(Integer idClient);
}