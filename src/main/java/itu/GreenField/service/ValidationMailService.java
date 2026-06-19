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

        Client client = clientRepository.findByMail(email);

        if (client == null) {
            throw new RuntimeException("Client introuvable : " + email);
        }

        String code = String.format("%05d",
                (int) (Math.random() * 100000));

        ValidationMail validationMail = new ValidationMail();
        validationMail.setClient(client);
        validationMail.setToken(code);
        validationMail.setEstVerifie(false);
        validationMail.setDateExpiration(
                LocalDateTime.now().plusMinutes(10));

        validationMailRepository.save(validationMail);

        envoiEmail.envoyerEmailAsync(
                "GreenField",
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                email,
                "Validation de votre compte",
                "Votre code de validation est : " + code
                        + ".\nVeuillez le saisir pour confirmer votre compte.",
                null,
                false);
    }

    public boolean verifier(String code, String email) {

        ValidationMail v = validationMailRepository.findByEmail(email);
        if (v == null || !v.getToken().equals(code)) {
            return false;
        } else {
            Client clientt = clientRepository.findByMail(email);
            clientt.setEstVerifie(true);
            clientRepository.save(clientt);
            v.setEstVerifie(true);
            validationMailRepository.save(v);
            return true;
        }

    }

}
