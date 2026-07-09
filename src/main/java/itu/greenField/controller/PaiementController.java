package itu.greenField.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
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
import itu.greenField.service.CommandesService;
import itu.greenField.service.PaiementService;

@Controller
@RequestMapping("paiements")
public class PaiementController {
    private final PaiementService paiementService;
    private final CommandesService commandesService;

    public PaiementController(PaiementService paiementService, CommandesService commandesService) {
        this.paiementService = paiementService;
        this.commandesService = commandesService;
    }

    @GetMapping("/choix/{commandeId}")
    public ModelAndView showChoixPaiement(@PathVariable("commandeId") Integer commandeId) {
        ModelAndView mv = new ModelAndView("front/paiement/choixPaiement");
        mv.addObject("commande", commandesService.findById(commandeId));
        return mv;
    }

    @GetMapping("/avance/{commandeId}")
    public ModelAndView showFormAvance(@PathVariable("commandeId") Integer commandeId) {
        ModelAndView mv = new ModelAndView("front/paiement/formAvance");
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
            redirectAttributes.addFlashAttribute("succes", "Avance enregistree avec succes.");
            return "redirect:/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/paiements/avance/" + commandeId;
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
        try {
            Paiement paiement = paiementService.payerTotalOuReste(commandeId, form.getLignes());
            redirectAttributes.addFlashAttribute("succes", "Paiement enregistre avec succes.");
            return "redirect:/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/paiements/total/" + commandeId;
        }
    }

    @PostMapping("/reste/{commandeId}")
    public String payerReste(@PathVariable("commandeId") Integer commandeId,
            @ModelAttribute("paiementFormDto") PaiementFormDto form,
            RedirectAttributes redirectAttributes) {
        try {
            Paiement paiement = paiementService.payerTotalOuReste(commandeId, form.getLignes());
            redirectAttributes.addFlashAttribute("succes", "Reste a payer enregistre avec succes.");
            return "redirect:/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/paiements/reste/" + commandeId;
        }
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listPaiements(@RequestParam(name = "statut", required = false) StatutPaiement statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ModelAndView mv = new ModelAndView("front/paiement/listePaiement");
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
        ModelAndView mv = new ModelAndView("front/paiement/detailPaiement");
        Paiement paiement = paiementService.findById(id);
        mv.addObject("paiement", paiement);
        mv.addObject("filles", paiementService.findFilles(paiement));
        mv.addObject("montantTotal", paiementService.getMontantTotalCommande(paiement.getCommande()));
        mv.addObject("montantPaye", paiementService.getMontantPaye(paiement));
        mv.addObject("montantRestant", paiementService.getMontantRestant(paiement.getCommande()));
        mv.addObject("paiementService", paiementService);
        return mv;
    }

    @PostMapping("/confirmer-espece/{id}")
    public String confirmerPaiementEspece(@PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            Paiement paiement = paiementService.confirmerPaiementEspece(id);
            redirectAttributes.addFlashAttribute("succes", "Paiement en espece confirme.");
            return "redirect:/paiements/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert", e.getMessage());
            return "redirect:/paiements/list";
        }
    }

    private ModelAndView buildFormTotal(Integer commandeId, boolean reste) {
        ModelAndView mv = new ModelAndView("front/paiement/formPaiementTotal");
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
