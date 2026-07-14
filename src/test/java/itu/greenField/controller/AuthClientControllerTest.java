package itu.greenField.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import itu.greenField.repository.ClientRepository;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.StatutCommandeRepository;
import itu.greenField.service.CommandeFrontService;
import itu.greenField.service.EmployesService;
import itu.greenField.service.PanierService;
import itu.greenField.service.ValidationMailService;
import itu.greenField.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

class AuthClientControllerTest {

    @Test
    void verifierMailUsesSessionEmailWhenModelHasNoEmail() {
        AuthClientController controller = new AuthClientController(
                mock(ClientRepository.class),
                mock(ValidationService.class),
                mock(ValidationMailService.class),
                mock(PanierService.class),
                mock(CommandeFrontService.class),
                mock(CommandesRepository.class),
                mock(StatutCommandeRepository.class),
                mock(EmployesService.class)
        );

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("validationEmail", "client@example.com");
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.verifierMail(session, model);

        assertEquals("front/auth/email", viewName);
        assertEquals("client@example.com", model.getAttribute("email"));
    }
}
