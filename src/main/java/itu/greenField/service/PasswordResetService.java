package itu.greenField.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Client;
import itu.greenField.model.Employes;
import itu.greenField.model.PasswordResetToken;
import itu.greenField.repository.ClientRepository;
import itu.greenField.repository.EmployesRepository;
import itu.greenField.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ClientRepository clientRepository;
    private final EmployesRepository employesRepository;
    private final EnvoiEmail envoiEmail;

    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository,
            ClientRepository clientRepository, EmployesRepository employesRepository, EnvoiEmail envoiEmail) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.clientRepository = clientRepository;
        this.employesRepository = employesRepository;
        this.envoiEmail = envoiEmail;
    }

    @Transactional
    public boolean requestReset(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String normalized = email.trim().toLowerCase();
        boolean exists = clientRepository.findByMail(normalized) != null
                || employesRepository.findByMailIgnoreCase(normalized).isPresent();
        if (!exists) {
            return false;
        }

        String token = generateToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(normalized);
        resetToken.setToken(token);
        resetToken.setUsed(false);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);

        String subject = "Réinitialisation de votre mot de passe GreenField";
        String content = "Bonjour,\n\nVous avez demandé une réinitialisation de mot de passe.\n"
                + "Votre code de vérification est : " + token + "\n\n"
                + "Ce code expire dans 15 minutes.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail.";

        envoiEmail.envoyerEmailAsync(
                "GreenField",
                "nyandomm6@gmail.com",
                "wite ymxy elbc usra",
                normalized,
                subject,
                content,
                null,
                false);

        return true;
    }

    @Transactional
    public boolean resetPassword(String email, String token, String newPassword) {
        if (email == null || token == null || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        String normalized = email.trim().toLowerCase();
        Optional<PasswordResetToken> latest = passwordResetTokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(normalized);
        if (latest.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = latest.get();
        if (!token.equals(resetToken.getToken()) || resetToken.isExpired()) {
            return false;
        }

        Client client = clientRepository.findByMail(normalized);
        if (client != null) {
            client.setMotdepasse(newPassword);
            clientRepository.save(client);
        } else {
            Optional<Employes> employe = employesRepository.findByMailIgnoreCase(normalized);
            if (employe.isEmpty()) {
                return false;
            }
            Employes existing = employe.get();
            existing.setMotdepasse(newPassword);
            employesRepository.save(existing);
        }

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        return true;
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
