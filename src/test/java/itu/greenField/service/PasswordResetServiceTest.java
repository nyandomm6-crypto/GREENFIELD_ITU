package itu.greenField.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import itu.greenField.model.Client;
import itu.greenField.model.Employes;
import itu.greenField.model.PasswordResetToken;
import itu.greenField.repository.ClientRepository;
import itu.greenField.repository.EmployesRepository;
import itu.greenField.repository.PasswordResetTokenRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployesRepository employesRepository;

    @Mock
    private EnvoiEmail envoiEmail;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void requestReset_shouldCreateTokenAndSendEmailWhenClientExists() {
        Client client = new Client();
        client.setMail("client@test.com");
        when(clientRepository.findByMail("client@test.com")).thenReturn(client);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.requestReset("client@test.com");

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(envoiEmail).envoyerEmailAsync(anyString(), anyString(), anyString(), eq("client@test.com"),
                contains("mot de passe"), anyString(), isNull(), eq(false));
    }

    @Test
    void resetPassword_shouldUpdateClientPasswordWhenCodeIsValid() {
        Client client = new Client();
        client.setMail("client@test.com");
        client.setMotdepasse("old-password");

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail("client@test.com");
        token.setToken("123456");
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        when(passwordResetTokenRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("client@test.com"))
                .thenReturn(Optional.of(token));
        when(clientRepository.findByMail("client@test.com")).thenReturn(client);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = passwordResetService.resetPassword("client@test.com", "123456", "new-password");

        assertTrue(result);
        assertEquals("new-password", client.getMotdepasse());
        verify(clientRepository).save(client);
    }
}
