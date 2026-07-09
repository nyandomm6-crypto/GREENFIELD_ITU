package itu.greenField.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.CategorieProduit;
import itu.greenField.model.Produit;
import itu.greenField.repository.CategorieProduitRepository;
import itu.greenField.service.AvisProduitService;
import itu.greenField.service.ProduitService;
import jakarta.servlet.http.HttpSession;

class ProduitControllerTest {

    @Test
    void shouldExposePaginationAttributesOnProductsPage() {
        ProduitService produitService = mock(ProduitService.class);
        CategorieProduitRepository categorieProduitRepository = mock(CategorieProduitRepository.class);
        ProduitController controller = new ProduitController(produitService, categorieProduitRepository,
                mock(AvisProduitService.class));
        Model model = mock(Model.class);

        Produit produit = new Produit();
        produit.setId(1);
        produit.setNom("Tomate");

        Page<Produit> page = new PageImpl<>(List.of(produit), PageRequest.of(2, 12), 1);
        when(produitService.rechercherProduitsPage(any(), any(), any())).thenReturn(page);
        when(produitService.calculerStock(any())).thenReturn(5);
        when(categorieProduitRepository.findAll()).thenReturn(List.of(new CategorieProduit()));

        String view = controller.listerProduits(null, null, 2, 12, model);

        assertEquals("front/produits/liste", view);
        assertNotNull(view);
    }

    @Test
    void shouldRedirectToLoginWhenClientIsNotAuthenticated() {
        ProduitService produitService = mock(ProduitService.class);
        CategorieProduitRepository categorieProduitRepository = mock(CategorieProduitRepository.class);
        AvisProduitService avisProduitService = mock(AvisProduitService.class);
        ProduitController controller = new ProduitController(produitService, categorieProduitRepository,
                avisProduitService);
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.posterAvis(7, "Très bien", 5, session, redirectAttributes);

        assertEquals("redirect:/login?redirect=/produits/7", view);
    }
}
