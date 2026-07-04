package itu.greenField.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.Livraison;
import itu.greenField.model.LivraisonFille;
import itu.greenField.model.Paiement;
import itu.greenField.model.PaiementFille;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.TypePayement;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.LivraisonFilleRepository;
import itu.greenField.repository.PaiementRepository;
import itu.greenField.service.LivraisonService;
import itu.greenField.service.PaiementService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/livreurs")
@RequiredArgsConstructor
public class DashboardLivreurController {

    private final LivraisonService livraisonService;
    private final PaiementService paiementService;
    private final LivraisonFilleRepository livraisonFilleRepository;
    private final CommandesRepository commandesRepository;
    private final PaiementRepository paiementRepository;

    @GetMapping("/historique-livraisons")
    public String historiqueLivraisons(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("livreur", employe);
        return "back/livraison/historique-livraisons";
    }

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("livreur", employe);
        return "back/livraison/profil";
    }

    @GetMapping("/dashboard")
    public String accueil(HttpSession session, Model model) {
        Employes employes = (Employes) session.getAttribute("employe");
        if (employes == null || !employes.getRole().equals(FRole.Livreur)) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("livreur", employes);
        return "back/livraison/dashboard";
    }

    @GetMapping("/livraisons")
    public String mesLivraisons(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("livreur", employe);
        model.addAttribute("livraisons", livraisonService.findByLivreur(employe));
        return "back/livraison/mes-livraisons";
    }

    @GetMapping("/livraisons/{id}")
    public String detailLivraison(@PathVariable Integer id,
            HttpSession session,
            Model model) {

        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        Livraison livraison = livraisonService.getLivraisonById(id);

        if (livraison == null || !livraison.getLivreur().getId().equals(employe.getId())) {
            return "redirect:/livreurs/livraisons";
        }

        model.addAttribute("livreur", employe);
        model.addAttribute("livraison", livraison);

        return "back/livraison/detail-livraison";
    }

    @PostMapping("/livraisonFille/valider")
    public String validerfille(@RequestParam Integer idLivraisonFille,
            HttpSession session) {

        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        LivraisonFille livraisonFille = livraisonFilleRepository.findById(idLivraisonFille)
                .orElseThrow(() -> new RuntimeException("LivraisonFille introuvable"));

        Integer idCommandes = livraisonFille.getCommande().getId();

        if (paiementService.resteByCommande(idCommandes)
                .compareTo(BigDecimal.ZERO) <= 0) {

            livraisonService.validerFille(idLivraisonFille);

            return "redirect:/livreurs/livraisons/" + livraisonFille.getLivraison().getId();
        }

        return "redirect:/livreurs/livraisons/paiement/" + idCommandes;
    }

    @GetMapping("/livraisons/paiement/{idCommande}")
    public String pagePaiement(@PathVariable Integer idCommande, Model model, HttpSession session) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        Commandes commande = commandesRepository.findById(idCommande)
                .orElseThrow();

        Paiement paiement = paiementRepository.findByCommande(commande).orElse(null);

        BigDecimal reste = paiementService.resteByCommande(idCommande);

        model.addAttribute("commande", commande);
        model.addAttribute("paiement", paiement);
        model.addAttribute("reste", reste);
        model.addAttribute("huhu", 300);
        model.addAttribute("typesPaiement", TypePayement.values());

        return "back/paiement/paiement-livraison";
    }

    @PostMapping("/paiements/ajouter-multiple")
    public String ajouterPaiement(
            @RequestParam Integer idCommande,
            @RequestParam("typePayement") List<TypePayement> types,
            @RequestParam("valeur") List<BigDecimal> valeurs, HttpSession session) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        paiementService.ajouterPayement(types, valeurs, idCommande);
        return "redirect:/paiements/page?idCommande=" + idCommande;
    }

    @PostMapping("/livraisons/valider")
    String valider(@PathVariable Integer id,
            HttpSession session,
            Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        return "";
    }

}
