package itu.GreenField.controller;

import itu.GreenField.dto.CreerTransfertRequest;
import itu.GreenField.dto.DemandeTransfertRequest;
import itu.GreenField.dto.ProduitQuantiteDTO;
import itu.GreenField.model.Produit;
import itu.GreenField.service.TransfertService;
import itu.GreenField.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/views")
public class TransfertViewController {

    @Autowired
    private TransfertService transfertService;

    @Autowired
    private ProduitRepository produitRepository;

    /**
     * Page : Liste des transferts avec filtres
     */
    @GetMapping("/transferts")
    public String listeTransferts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) String codePointDeVente,
            Model model) {

        model.addAttribute("transferts",
                transfertService.listerTransferts(dateDebut, dateFin, codePointDeVente));
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        model.addAttribute("codePointDeVente", codePointDeVente);
        model.addAttribute("pageTitle", "Liste des Transferts");

        return "transferts/liste";
    }

    /**
     * Page : Détail d'un transfert
     */
    @GetMapping("/transferts/{id}")
    public String detailTransfert(@PathVariable Long id, Model model) {
        model.addAttribute("transfert", transfertService.detailTransfert(id));
        model.addAttribute("pageTitle", "Détail du Transfert #" + id);
        return "transferts/detail";
    }

    /**
     * Page : Formulaire de demande de transfert
     */
    @GetMapping("/transferts/demande")
    public String formulaireDemande(Model model) {
        model.addAttribute("demande", new DemandeTransfertRequest());
        model.addAttribute("produits", produitRepository.findAll());
        model.addAttribute("pageTitle", "Demander un Transfert");
        return "transferts/demande";
    }

    /**
     * Traitement : Soumission du formulaire de demande
     */
    @PostMapping("/transferts/demande")
    public String traiterDemande(@ModelAttribute DemandeTransfertRequest demande, Model model) {
        try {
            transfertService.demandeTransfert(demande);
            model.addAttribute("success", "✅ Demande de transfert créée avec succès !");
            model.addAttribute("pageTitle", "Demande créée");
            return "transferts/success";
        } catch (Exception e) {
            model.addAttribute("error", "❌ Erreur : " + e.getMessage());
            model.addAttribute("produits", produitRepository.findAll());
            return "transferts/demande";
        }
    }

    /**
     * Page : Formulaire de création de transfert
     */
    @GetMapping("/transferts/creer")
    public String formulaireCreation(Model model) {
        CreerTransfertRequest transfert = new CreerTransfertRequest();
        transfert.setProduits(new ArrayList<>());
        model.addAttribute("transfert", transfert);
        model.addAttribute("produits", produitRepository.findAll());
        model.addAttribute("pageTitle", "Créer un Transfert");
        return "transferts/creer";
    }

    /**
     * Traitement : Soumission du formulaire de création
     */
    @PostMapping("/transferts/creer")
    public String traiterCreation(@ModelAttribute CreerTransfertRequest transfert, Model model) {
        try {
            transfertService.creerTransfert(transfert);
            model.addAttribute("success", "✅ Transfert créé avec succès !");
            model.addAttribute("pageTitle", "Transfert créé");
            return "transferts/success";
        } catch (Exception e) {
            model.addAttribute("error", "❌ Erreur : " + e.getMessage());
            model.addAttribute("produits", produitRepository.findAll());
            return "transferts/creer";
        }
    }
}
