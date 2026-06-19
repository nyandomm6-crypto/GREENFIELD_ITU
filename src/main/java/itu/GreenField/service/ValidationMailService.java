package itu.GreenField.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import itu.GreenField.model.Client;
import itu.GreenField.model.ValidationMail;
import itu.GreenField.repository.ClientRepository;
import itu.GreenField.repository.ValidationMailRepository;

@Service
public class ValidationMailService {
    private EnvoiEmail envoiEmail;
    private ValidationMailRepository validationMailRepository;
    private ClientRepository clientRepository;

    public ValidationMailService(EnvoiEmail envoiEmail, ValidationMailRepository validationMailRepository,
            ClientRepository clientRepository) {
        this.envoiEmail = envoiEmail;
        this.validationMailRepository = validationMailRepository;
        this.clientRepository = clientRepository;
    }

    public void send(String email) {
        // get client
        Client client = clientRepository.findByMail(email);
        // creer validation
        ValidationMail validationMail = new ValidationMail();
        validationMail.setClient(client);
        validationMail.setDateExpiration(LocalDateTime.now().plusMinutes(10));
        // save
        validationMailRepository.save(validationMail);
        ValidationMail validationOk = validationMailRepository.findByClientId(client.getId());

        envoiEmail.envoyerEmailAsync(
                "GreenField",
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                email,
                "Validation de votre compte",
                "Votre code de validation est : " + validationOk.getToken()
                        + ".\nVeuillez le saisir pour confirmer votre compte.",
                null,
                false);
    }
}
