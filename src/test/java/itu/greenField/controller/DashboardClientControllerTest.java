package itu.greenField.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import itu.greenField.service.BanniereService;
import itu.greenField.service.CategorieProduitService;
import itu.greenField.service.FaqService;
import itu.greenField.service.FeatureService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.PubliciteService;
import itu.greenField.service.TemoignageService;
import jakarta.servlet.http.HttpSession;

class DashboardClientControllerTest {

    @Test
    void shouldExposeTestimonialsOnHomePage() {
        ProduitService produitService = org.mockito.Mockito.mock(ProduitService.class);
        CategorieProduitService categorieService = org.mockito.Mockito.mock(CategorieProduitService.class);
        FeatureService featureService = org.mockito.Mockito.mock(FeatureService.class);
        PubliciteService publiciteService = org.mockito.Mockito.mock(PubliciteService.class);
        BanniereService banniereService = org.mockito.Mockito.mock(BanniereService.class);
        FaqService faqService = org.mockito.Mockito.mock(FaqService.class);
        TemoignageService temoignageService = org.mockito.Mockito.mock(TemoignageService.class);
        DashboardClientController controller = new DashboardClientController(
                produitService,
                categorieService,
                featureService,
                publiciteService,
                banniereService,
                faqService,
                temoignageService);

        org.mockito.Mockito.when(temoignageService.findAllActifs()).thenReturn(List.of());

        ExtendedModelMap model = new ExtendedModelMap();
        String view = controller.accueil(org.mockito.Mockito.mock(HttpSession.class), model);

        assertEquals("front/accueil/acc2", view);
        assertTrue(model.asMap().containsKey("temoignages"));
    }
}
