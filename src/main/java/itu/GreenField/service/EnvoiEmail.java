package itu.GreenField.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Service
public class EnvoiEmail {

    public void envoyerEmail(
            String nomEntreprise,
            String email,
            String motDePasse,
            String destinataire,
            String sujet,
            String contenu,
            String cheminPdf)
            throws Exception {

        envoyer(nomEntreprise, email, motDePasse, destinataire, sujet, contenu, cheminPdf);
    }

    @Async
    public void envoyerEmailAsync(
            String nomEntreprise,
            String email,
            String motDePasse,
            String destinataire,
            String sujet,
            String contenu,
            String cheminPdf,
            boolean supprimerPieceJointeApresEnvoi) {

        try {
            envoyerEmail(nomEntreprise, email, motDePasse, destinataire, sujet, contenu, cheminPdf);
            System.out.println("Email envoye avec succes.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (supprimerPieceJointeApresEnvoi && cheminPdf != null && !cheminPdf.isBlank()) {
                try {
                    Files.deleteIfExists(Path.of(cheminPdf));
                } catch (Exception e) {
                    System.err.println("Impossible de supprimer le fichier temporaire : " + e.getMessage());
                }
            }
        }
    }

    private void envoyer(
            String nomEntreprise,
            String email,
            String motDePasse,
            String destinataire,
            String sujet,
            String contenu,
            String cheminPdf)
            throws Exception {

        Session session = creerSession(email, motDePasse);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(email, nomEntreprise));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destinataire));

        message.setSubject(sujet, "UTF-8");

        message.setSentDate(new java.util.Date());

        if (cheminPdf == null || cheminPdf.isBlank()) {

            message.setText(contenu, "UTF-8");

        } else {

            MimeBodyPart texte = new MimeBodyPart();
            texte.setText(contenu, "UTF-8");

            MimeBodyPart pdf = new MimeBodyPart();
            pdf.setDataHandler(
                    new DataHandler(
                            new FileDataSource(cheminPdf)));

            pdf.setFileName(
                    new java.io.File(cheminPdf).getName());

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(texte);
            multipart.addBodyPart(pdf);

            message.setContent(multipart);
        }

        Transport.send(message);
    }

    private Session creerSession(String email, String motDePasse) {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                email,
                                motDePasse.replace(" ", ""));
                    }
                });
    }

}
