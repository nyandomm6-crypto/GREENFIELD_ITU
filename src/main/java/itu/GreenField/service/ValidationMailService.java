package itu.GreenField.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationMailService {
    private EnvoiEmail envoiEmail;

    public ValidationMailService(EnvoiEmail envoiEmail) {
        this.envoiEmail = envoiEmail;
    }

    public void send(String email) {
        envoiEmail.envoyerEmailAsync(
                "GreenField",
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                email,
                "Validation de votre compte",
                "Votre code de validation est : 37398",
                null,
                false);
    }
}
