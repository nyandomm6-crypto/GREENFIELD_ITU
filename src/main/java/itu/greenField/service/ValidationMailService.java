package itu.greenField.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import itu.greenField.model.Client;
import itu.greenField.model.ValidationMail;
import itu.greenField.repository.ClientRepository;
import itu.greenField.repository.ValidationMailRepository;

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

        String nomClient = client.getPrenom() != null && !client.getPrenom().isBlank()
                ? client.getPrenom()
                : "cher client";
        String prenomClient = client.getNom() != null && !client.getNom().isBlank()
                ? client.getNom()
                : "";
        String nomComplet = (prenomClient + " " + nomClient).trim();

        String contenu = "Bonjour " + nomComplet + ",\n\n"
                + "Merci pour votre inscription sur GreenField.\n"
                + "Pour finaliser votre création de compte, veuillez utiliser le code de vérification suivant :\n\n"
                + code + "\n\n"
                + "Ce code est valable pendant 10 minutes.\n"
                + "Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet e-mail.\n\n"
                + "Cordialement,\n"
                + "L'équipe GreenField";

        envoiEmail.envoyerEmailAsync(
                "GreenField",
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                email,
                "Validation de votre compte GreenField",
                contenu,
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
