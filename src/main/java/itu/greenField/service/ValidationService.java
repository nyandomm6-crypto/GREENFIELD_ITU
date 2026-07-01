package itu.greenField.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    public Map<String, String> validationSignup(String email,
            String motDePasse,
            String nom,
            String prenom,
            String adresse,
            String contact) {

        Map<String, String> errors = new HashMap<>();

        if (nom == null || nom.trim().isEmpty()) {
            errors.put("nom", "Nom obligatoire");
        }

        if (prenom == null || prenom.trim().isEmpty()) {
            errors.put("prenom", "Prénom obligatoire");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Email invalide");
        }

        if (adresse == null || adresse.trim().isEmpty()) {
            errors.put("adresse", "Adresse obligatoire");
        }

        if (contact == null || contact.length() < 8) {
            errors.put("contact", "Contact trop court");
        }

        if (motDePasse == null || motDePasse.length() < 6) {
            errors.put("motDePasse", "Mot de passe trop court (min 6)");
        }

        return errors;
    }
}
