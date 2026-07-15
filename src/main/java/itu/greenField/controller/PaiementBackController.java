package itu.greenField.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.dto.PaiementFormDto;
import itu.greenField.dto.PaiementLigneDto;
import itu.greenField.model.Commandes;
import itu.greenField.model.Paiement;
import itu.greenField.model.StatutPaiement;
import itu.greenField.model.TypePayement;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.CommandesService;
import itu.greenField.service.PaiementService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Paiements côté back office (administrateur) : liste complète, saisie et
 * confirmation manuelle des encaissements. Le front office ({@link
 * PaiementController}) ne sert que le parcours du client.
 */
@Controller
@RequestMapping("/back/paiements")
@RequiredArgsConstructor
public class PaiementBackController {

    private final PaiementService paiementService;
    private final CommandesService commandesService;

    @ModelAttribute
    public void garde(HttpSession session) {
        if (!AuthGuard.isAdmin(session)) {
            throw new AuthGuard.AccesRefuseException();
        }
    }

    @ExceptionHandler(AuthGuard.AccesRefuseException.class)
    public String accesRefuse() {
        return "redirect:/emp/login";
    }

    @GetMapping({ "", "/", "/list" })
    public ModelAndView listPaiements(@RequestParam(name = "statut", required = false) StatutPaiement statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ModelAndView mv = new ModelAndView("back/paiement/listePaiement");
        List<Paiement> paiements = paiementService.findByStatut(statut);

        // Pagination en mémoire (« nombre d'éléments par page »)
        if (size <= 0) {
            size = 10;
        }
        int total = paiements.size();
        int totalPages = (int) Math.ceil((double) total / size);
        if (page < 0) {
            page = 0;
        }
        if (totalPages > 0 && page >= totalPages) {
            page = totalPages - 1;
        }
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);

        mv.addObject("paiements", paiements.subList(from, to));
        mv.addObject("statut", statut);
        mv.addObject("statutsPaiement", StatutPaiement.values());
        mv.addObject("paiementService", paiementService);
        mv.addObject("page", page);
        mv.addObject("size", size);
        mv.addObject("totalPages", totalPages);
        mv.addObject("totalElements", total);
        return mv;
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detailPaiement(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("back/paiement/detailPaiement");
        Paiement paiement = paiementService.findById(id);
        mv.addObject("paiement", paiement);
        mv.addObject("filles", paiementService.findFilles(paiement));
        mv.addObject("montantTotal", paiementService.getMontantTotalCommande(paiement.getCommande()));
        mv.addObject("montantPaye", paiementService.getMontantPaye(paiement));
        mv.addObject("montantRestant", paiementService.getMontantRestant(paiement.getCommande()));
        mv.addObject("montantEnAttente", paiementService.getMontantEnAttente(paiement));
        mv.addObject("paiementService", paiementService);
        return mv;
    }

    @GetMapping("/choix/{commandeId}")
    public ModelAndView showChoixPaiement(@PathVariable("commandeId") Integer commandeId) {
        ModelAndView mv = new ModelAndView("back/paiement/choixPaiement");
        mv.addObject("commande", commandesService.findById(commandeId));
        return mv;
    }

    @GetMapping("/avance/{commandeId}")
    public ModelAndView showFormAvance(@PathVariable("commandeId") Integer commandeId) {
        ModelAndView mv = new ModelAndView("back/paiement/formAvance");
        Commandes commande = commandesService.findById(commandeId);
        mv.addObject("commande", commande);
        mv.addObject("montantTotal", paiementService.getMontantTotalCommande(commande));
        mv.addObject("avanceAPayer", paiementService.calculerAvance(commande));
        mv.addObject("typesPaiement", List.of(TypePayement.Mobile_Money));
        mv.addObject("numerosTransfert", paiementService.getNumerosTransfertMobileMoney());
        return mv;
    }

    @PostMapping("/avance/{commandeId}")
    public String payerAvance(@PathVariable("commandeId") Integer commandeId,
            @RequestParam("montant") BigDecimal montant,
            RedirectAttributes redirectAttributes) {
        try {
            Paiement paiement = paiementService.payerAvance(commandeId, montant);
            redirectAttributes.addFlashAttribute("succes",
                    "Avance enregistrée. Elle reste à confirmer pour être encaissée.");
            return "redirect:/back/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/back/paiements/avance/" + commandeId;
        }
    }

    @GetMapping("/total/{commandeId}")
    public ModelAndView showFormTotal(@PathVariable("commandeId") Integer commandeId) {
        return buildFormTotal(commandeId, false);
    }

    @GetMapping("/reste/{commandeId}")
    public ModelAndView showFormReste(@PathVariable("commandeId") Integer commandeId) {
        return buildFormTotal(commandeId, true);
    }

    @PostMapping("/total/{commandeId}")
    public String payerTotal(@PathVariable("commandeId") Integer commandeId,
            @ModelAttribute("paiementFormDto") PaiementFormDto form,
            RedirectAttributes redirectAttributes) {
        return enregistrer(commandeId, form, redirectAttributes, "total");
    }

    @PostMapping("/reste/{commandeId}")
    public String payerReste(@PathVariable("commandeId") Integer commandeId,
            @ModelAttribute("paiementFormDto") PaiementFormDto form,
            RedirectAttributes redirectAttributes) {
        return enregistrer(commandeId, form, redirectAttributes, "reste");
    }

    /**
     * Confirmation manuelle d'un encaissement (espèce ou mobile money). Le
     * montant saisi est celui réellement reçu : c'est lui qui est encaissé.
     */
    @PostMapping("/confirmer/{id}")
    public String confirmer(@PathVariable("id") Integer id,
            @RequestParam(name = "filleId", required = false) Integer filleId,
            @RequestParam(name = "montantRecu", required = false) BigDecimal montantRecu,
            RedirectAttributes redirectAttributes) {
        try {
            Paiement paiement = paiementService.confirmerPaiement(id, filleId, montantRecu);
            redirectAttributes.addFlashAttribute("succes",
                    "Encaissement confirmé, l'entrée de trésorerie a été créée.");
            return "redirect:/back/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/back/paiements/detail/" + id;
        }
    }

    private String enregistrer(Integer commandeId, PaiementFormDto form, RedirectAttributes redirectAttributes,
            String vue) {
        try {
            Paiement paiement = paiementService.payerTotalOuReste(commandeId, form.getLignes());
            redirectAttributes.addFlashAttribute("succes", paiementService.aDesLignesEnAttente(paiement)
                    ? "Paiement enregistré. Il reste à confirmer pour être encaissé."
                    : "Paiement enregistré avec succès.");
            return "redirect:/back/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/back/paiements/" + vue + "/" + commandeId;
        }
    }

    private ModelAndView buildFormTotal(Integer commandeId, boolean reste) {
        ModelAndView mv = new ModelAndView("back/paiement/formPaiementTotal");
        Commandes commande = commandesService.findById(commandeId);
        Paiement paiement = paiementService.findByCommandeId(commandeId);
        BigDecimal montantRestant = paiementService.getMontantRestant(commande);

        PaiementFormDto dto = new PaiementFormDto();
        dto.setCommandeId(commandeId);
        dto.getLignes().add(new PaiementLigneDto(TypePayement.Mobile_Money, montantRestant));

        mv.addObject("commande", commande);
        mv.addObject("paiement", paiement);
        mv.addObject("fillesExistantes", paiementService.findFilles(paiement));
        mv.addObject("paiementFormDto", dto);
        mv.addObject("typesPaiement", TypePayement.values());
        mv.addObject("numerosTransfert", paiementService.getNumerosTransfertMobileMoney());
        mv.addObject("montantTotal", paiementService.getMontantTotalCommande(commande));
        mv.addObject("montantPaye", paiementService.getMontantPaye(paiement));
        mv.addObject("montantRestant", montantRestant);
        mv.addObject("modeReste", reste);
        return mv;
    }
}
