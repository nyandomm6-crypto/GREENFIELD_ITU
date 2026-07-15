package itu.greenField.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.dto.PaiementLigneDto;
import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.Livraison;
import itu.greenField.model.LivraisonFille;
import itu.greenField.model.Paiement;
import itu.greenField.model.StatutLivraison;
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
    public String historiqueLivraisons(
            HttpSession session,
            Model model,
            @RequestParam(required = false) StatutLivraison statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        List<Livraison> livraisons = livraisonService.findByLivreur(employe).stream()
                .filter(l -> statut == null || l.getStatutLivraison() == statut)
                .filter(l -> dateDebut == null || (l.getDateLivraison() != null
                        && !l.getDateLivraison().toLocalDate().isBefore(dateDebut)))
                .filter(l -> dateFin == null || (l.getDateLivraison() != null
                        && !l.getDateLivraison().toLocalDate().isAfter(dateFin)))
                .sorted((a, b) -> {
                    if (a.getDateLivraison() == null || b.getDateLivraison() == null) {
                        return 0;
                    }
                    return b.getDateLivraison().compareTo(a.getDateLivraison());
                })
                .toList();

        model.addAttribute("livreur", employe);
        model.addAttribute("livraisons", livraisons);
        model.addAttribute("statuts", List.of(StatutLivraison.values()));
        model.addAttribute("statut", statut);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
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
        model.addAttribute("livraisons", livraisonService.findByLivreurDispo(employe));
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
        if (!livraisonService.isMyLivraisonFille(id, employe)) {
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

        if (paiementService.getMontantRestant(livraisonFille.getCommande())
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

        // Le reste inclut les frais de livraison : c'est le même montant que celui
        // exigé à l'enregistrement du paiement.
        BigDecimal reste = paiementService.getMontantRestant(commande);

        model.addAttribute("commande", commande);
        model.addAttribute("paiement", paiement);
        model.addAttribute("reste", reste);
        model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(commande));
        model.addAttribute("typesPaiement", TypePayement.values());
        if (!livraisonService.isMyCommande(idCommande, employe)) {
            return "redirect:/livreurs/livraisons";
        }

        return "back/paiement/paiement-livraison";
    }

    /**
     * Saisie du paiement par le livreur. Le flux est celui du caissier : la
     * saisie n'encaisse rien, elle crée des lignes en attente que le livreur
     * confirme ensuite. C'est la confirmation qui crée l'entrée de trésorerie,
     * et la clôture du paiement qui sort le stock de son point de vente.
     */
    @PostMapping("/paiements/ajouter-multiple")
    public String ajouterPaiement(
            @RequestParam Integer idCommande,
            @RequestParam("typePayement") List<TypePayement> types,
            @RequestParam("valeur") List<BigDecimal> valeurs, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        if (!livraisonService.isMyCommande(idCommande, employe)) {
            return "redirect:/livreurs/livraisons";
        }
        try {
            List<PaiementLigneDto> lignes = construireLignes(types, valeurs);
            Paiement paiement = paiementService.payerTotalOuReste(idCommande, lignes);
            redirectAttributes.addFlashAttribute("succes", paiementService.aDesLignesEnAttente(paiement)
                    ? "Paiement enregistré. Confirmez le montant reçu pour l'encaisser."
                    : "Paiement enregistré avec succès.");
            return "redirect:/livreurs/paiement/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/livreurs/livraisons/paiement/" + idCommande;
        }
    }

    /**
     * Confirmation d'un encaissement par le livreur : le montant saisi est celui
     * réellement reçu du client, c'est lui qui part en trésorerie.
     */
    @PostMapping("/paiement/confirmer/{id}")
    public String confirmerPaiement(@PathVariable Integer id,
            @RequestParam(required = false) Integer filleId,
            @RequestParam(required = false) BigDecimal montantRecu,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        Paiement paiement = paiementService.findById(id);
        if (paiement == null || paiement.getCommande() == null
                || !livraisonService.isMyCommande(paiement.getCommande().getId(), employe)) {
            return "redirect:/livreurs/livraisons";
        }
        try {
            paiementService.confirmerPaiement(id, filleId, montantRecu);
            redirectAttributes.addFlashAttribute("succes",
                    "Encaissement confirmé, l'entrée de trésorerie a été créée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/livreurs/paiement/detail/" + id;
    }

    @GetMapping("/paiement/detail/{id}")
    public String detailPaiement(@PathVariable Integer id, HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        Paiement paiement = paiementService.findById(id);
        if (paiement == null || paiement.getCommande() == null
                || !livraisonService.isMyCommande(paiement.getCommande().getId(), employe)) {
            return "redirect:/livreurs/livraisons";
        }
        model.addAttribute("livreur", employe);
        model.addAttribute("paiement", paiement);
        model.addAttribute("filles", paiementService.findFilles(paiement));
        model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(paiement.getCommande()));
        model.addAttribute("montantRestant", paiementService.getMontantRestant(paiement.getCommande()));
        model.addAttribute("montantEnAttente", paiementService.getMontantEnAttente(paiement));
        model.addAttribute("paiementService", paiementService);
        return "back/livraison/paiementDetail";
    }

    /** Assemble les lignes « type / montant » saisies dans le formulaire. */
    private List<PaiementLigneDto> construireLignes(List<TypePayement> types, List<BigDecimal> valeurs) {
        List<PaiementLigneDto> lignes = new ArrayList<>();
        for (int i = 0; i < types.size() && i < valeurs.size(); i++) {
            lignes.add(new PaiementLigneDto(types.get(i), valeurs.get(i)));
        }
        return lignes;
    }

    @PostMapping("/livraisons/valider")
    String valider(@RequestParam Integer idLivraison,
            HttpSession session,
            Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        livraisonService.valider(idLivraison);
        return "redirect:/livreurs/livraisons";
    }

}
