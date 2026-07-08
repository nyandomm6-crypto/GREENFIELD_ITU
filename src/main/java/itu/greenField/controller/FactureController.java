package itu.greenField.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import itu.greenField.model.Commandes;
import itu.greenField.service.CommandesService;
import itu.greenField.service.FacturePdfService;

/**
 * Génère une facture PDF (style ticket de grande surface) via OpenPDF.
 * Les informations du client ne sont volontairement pas affichées.
 */
@Controller
public class FactureController {

    private final CommandesService commandeService;
    private final FacturePdfService facturePdfService;

    public FactureController(CommandesService commandeService, FacturePdfService facturePdfService) {
        this.commandeService = commandeService;
        this.facturePdfService = facturePdfService;
    }

    @GetMapping("/facture/{idCommande}")
    public ResponseEntity<byte[]> facture(@PathVariable Integer idCommande) throws Exception {
        return genererReponse(idCommande);
    }

    // Compatibilité avec l'ancien lien /paiements/facture?idCommande=...
    @GetMapping("/paiements/facture")
    public ResponseEntity<byte[]> factureLegacy(@RequestParam Integer idCommande) throws Exception {
        return genererReponse(idCommande);
    }

    private ResponseEntity<byte[]> genererReponse(Integer idCommande) throws Exception {
        Commandes commande = commandeService.findById(idCommande);
        if (commande == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = facturePdfService.genererFacture(commande);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // inline : le PDF s'ouvre dans le navigateur (avec bouton de téléchargement)
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("facture_" + idCommande + ".pdf")
                .build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
