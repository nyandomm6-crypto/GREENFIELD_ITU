package itu.greenField.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import java.util.Set;

import itu.greenField.dto.CommandeBackFilterDto;
import itu.greenField.dto.CommandeBackFormDto;
import itu.greenField.dto.DetailCommandeBackDto;
import itu.greenField.dto.PaiementFormDto;
import itu.greenField.dto.PaiementLigneDto;
import itu.greenField.filtre.CalculOption;
import itu.greenField.filtre.FiltreDateBackCommandeOption;
import itu.greenField.filtre.FiltreNombreBackCommandeOption;
import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.ModeReception;
import itu.greenField.model.Paiement;
import itu.greenField.model.StatutPaiement;
import itu.greenField.model.TypeCommande;
import itu.greenField.model.TypePayement;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.CommandesService;
import itu.greenField.service.ProduitBackService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.PaiementService;
import itu.greenField.service.PointDeVenteService;
import itu.greenField.service.ProvinceLivraisonService;
import itu.greenField.service.StatutCommandeService;
import itu.greenField.repository.MvtStockRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/caissier")
@RequiredArgsConstructor
public class CaissierController {

    private final CommandesService commandesService;
    private final ProduitService produitService;
    private final ProduitBackService produitBackService;
    private final PointDeVenteService pointDeVenteService;
    private final ProvinceLivraisonService provinceLivraisonService;
    private final PaiementService paiementService;
    private final MvtStockRepository mvtStockRepository;
    private final StatutCommandeService statutCommandeService;
    private final CommandesService commandeService;

    /**
     * Vérifie que l'utilisateur connecté est bien un Caissier.
     * Renvoie l'employé, ou null si l'accès est refusé (le contrôleur redirige
     * alors).
     */
    private Employes requireCaissier(HttpSession session) {
        Employes employe = AuthGuard.current(session);
        if (employe == null || !FRole.Caissier.equals(employe.getRole())) {
            return null;
        }
        return employe;
    }

    /** Code du point de vente du caissier (peut être null s'il n'en a pas). */
    private String pdvCode(Employes employe) {
        return employe.getPointDeVente() != null ? employe.getPointDeVente().getCode() : null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        String code = pdvCode(employe);
        model.addAttribute("employe", employe);
        model.addAttribute("commandes", commandesService.findByPointDeVenteCreateur(code));
        model.addAttribute("paiements", paiementService.findByPointDeVente(code));
        return "back/caissier/dashboard";
    }

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        return "back/caissier/profil";
    }

    @GetMapping("/produits")
    public String produits(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("produits", produitService.getAllProduits());
        return "back/caissier/produits";
    }

    @GetMapping("/commandes")
    public ModelAndView listCommandes(HttpSession session) {
        ModelAndView mv = new ModelAndView("back/caissier/commandes");
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            mv.setViewName("/emp/login");
            return mv;
        }
        CommandeBackFilterDto filter = new CommandeBackFilterDto();
        filter.setPointDeVente(pdvCode(employe));
        
        generateListCommandesModel(mv, filter);
        return mv;
    }

    @GetMapping("/commande/detail/{id}")
    public String detailCommande(@PathVariable Integer id, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        Commandes commande = commandesService.findById(id);
        if (commande == null || commande.getPointDeVenteCreateur() == null
                || !pdvCode(employe).equals(commande.getPointDeVenteCreateur().getCode())) {
            redirectAttributes.addFlashAttribute("alert",
                    "Cette commande n'existe pas ou n'appartient pas à votre point de vente.");
            return "redirect:/caissier/commandes";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("commande", commande);
        Paiement paiementCommande = paiementService.findByCommandeId(commande.getId());
        model.addAttribute("reste", paiementService.getMontantRestant(commande));
        model.addAttribute("dejaPaye", paiementCommande != null);
        model.addAttribute("paiementCommande", paiementCommande);
        model.addAttribute("typePayements", TypePayement.values());
        boolean estLivrable = commande.getModeReception() == ModeReception.Retrait_Boutique
                && commande.getStatutActuel().getId() == 2;
                // && commande.getPointDeVenteRetrait() != null
                // && commande.getPointDeVenteRetrait().getCode().equals(pdvCode(employe));
        model.addAttribute("estLivrable", estLivrable);
        return "back/caissier/commandeDetail";
    }

    @GetMapping("/commande/livrer/{id}")
    public String livrerCommande(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            commandeService.livrerRetraitBoutiqueCommande(id);
            redirectAttributes.addFlashAttribute("succes", "Commande livrée avec succès.");
            return "redirect:/caissier/commande/detail/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/commande/detail/" + id;
        }
    }

    private void generateListCommandesModel(ModelAndView mv, CommandeBackFilterDto filter) {
        Page<Commandes> commandePage = commandeService.findWithDynamicFilters(filter);

        if (mv != null) {
            mv.addObject("commandes", commandePage.getContent());
            mv.addObject("totalPages", commandePage.getTotalPages());
            mv.addObject("hasPrevious", commandePage.hasPrevious());
            mv.addObject("hasNext", commandePage.hasNext());

            mv.addObject("commandeFilterDto", filter);

            mv.addObject("modeReceptionOptions", ModeReception.getAllModeReception());
            mv.addObject("typeCommandeOptions", TypeCommande.getAllTypeCommande());
            mv.addObject("calculOptions", CalculOption.values());
            mv.addObject("filtreDateOptions", FiltreDateBackCommandeOption.values());
            mv.addObject("filtreNombreOptions", FiltreNombreBackCommandeOption.values());
            mv.addObject("statutCommandeOptions", statutCommandeService.getAll());
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            mv.addObject("pointDeVenteOptions", pointDeVenteService.getAll());

            // Le reste n'est payable qu'une fois les encaissements déjà versés confirmés.
            Set<Integer> commandesAvecResteAPayer = commandePage.getContent().stream()
                    .filter(cmd -> paiementService.getMontantRestant(cmd).compareTo(java.math.BigDecimal.ZERO) > 0)
                    .filter(cmd -> cmd.getPaiement() != null
                            && cmd.getPaiement().getStatut() == itu.greenField.model.StatutPaiement.Reste
                            && !paiementService.aDesLignesEnAttente(cmd.getPaiement()))
                    .map(Commandes::getId)
                    .collect(Collectors.toSet());
            mv.addObject("commandesAvecResteAPayer", commandesAvecResteAPayer);
        }
    }

    @GetMapping("/paiements")
    public String paiements(@RequestParam(name = "statut", required = false) StatutPaiement statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        List<Paiement> paiements = paiementService.findByPointDeVente(pdvCode(employe));
        if (statut != null) {
            paiements = paiements.stream()
                    .filter(p -> statut.equals(p.getStatut()))
                    .collect(Collectors.toList());
        }

        // Pagination en mémoire (« nombre d'éléments par page »), comme côté admin.
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

        model.addAttribute("employe", employe);
        model.addAttribute("paiements", paiements.subList(from, to));
        model.addAttribute("statut", statut);
        model.addAttribute("statutsPaiement", StatutPaiement.values());
        model.addAttribute("paiementService", paiementService);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", total);
        return "back/caissier/paiements";
    }

    // ================= FLUX DE PAIEMENT (limité au point de vente) =================

    @GetMapping("/paiement/choix/{commandeId}")
    public String choixPaiement(@PathVariable Integer commandeId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            Commandes commande = paiementService.verifierCommandeDuPointDeVente(commandeId, pdvCode(employe));
            model.addAttribute("employe", employe);
            model.addAttribute("commande", commande);
            model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(commande));
            model.addAttribute("montantRestant", paiementService.getMontantRestant(commande));
            model.addAttribute("paiement", paiementService.findByCommandeId(commandeId));
            model.addAttribute("paiementService", paiementService);
            return "back/caissier/paiementChoix";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/commandes";
        }
    }

    @GetMapping("/paiement/avance/{commandeId}")
    public String formAvance(@PathVariable Integer commandeId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            Commandes commande = paiementService.verifierCommandeDuPointDeVente(commandeId, pdvCode(employe));
            model.addAttribute("employe", employe);
            model.addAttribute("commande", commande);
            model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(commande));
            model.addAttribute("avanceAPayer", paiementService.calculerAvance(commande));
            model.addAttribute("numerosTransfert", paiementService.getNumerosTransfertMobileMoney());
            return "back/caissier/paiementAvance";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/commandes";
        }
    }

    @PostMapping("/paiement/avance/{commandeId}")
    public String payerAvance(@PathVariable Integer commandeId, @RequestParam BigDecimal montant,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            Paiement paiement = paiementService.payerAvanceCaissier(commandeId, montant, pdvCode(employe));
            redirectAttributes.addFlashAttribute("succes",
                    "Avance enregistrée. Elle reste à confirmer pour être encaissée.");
            return "redirect:/caissier/paiement/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/paiement/avance/" + commandeId;
        }
    }

    @GetMapping("/paiement/total/{commandeId}")
    public String formTotal(@PathVariable Integer commandeId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        return formPaiement(commandeId, false, session, model, redirectAttributes);
    }

    @GetMapping("/paiement/reste/{commandeId}")
    public String formReste(@PathVariable Integer commandeId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        return formPaiement(commandeId, true, session, model, redirectAttributes);
    }

    @PostMapping("/paiement/total/{commandeId}")
    public String payerTotal(@PathVariable Integer commandeId,
            @ModelAttribute("paiementFormDto") PaiementFormDto form,
            HttpSession session, RedirectAttributes redirectAttributes) {
        return enregistrerPaiement(commandeId, form, session, redirectAttributes, "total");
    }

    @PostMapping("/paiement/reste/{commandeId}")
    public String payerReste(@PathVariable Integer commandeId,
            @ModelAttribute("paiementFormDto") PaiementFormDto form,
            HttpSession session, RedirectAttributes redirectAttributes) {
        return enregistrerPaiement(commandeId, form, session, redirectAttributes, "reste");
    }

    /**
     * Confirmation manuelle d'un encaissement (espèce ou mobile money) par le
     * caissier. Le montant saisi est celui réellement reçu : c'est lui qui est
     * encaissé.
     */
    @PostMapping("/paiement/confirmer/{id}")
    public String confirmerPaiement(@PathVariable Integer id,
            @RequestParam(required = false) Integer filleId,
            @RequestParam(required = false) BigDecimal montantRecu,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            paiementService.confirmerPaiementCaissier(id, filleId, montantRecu, pdvCode(employe));
            redirectAttributes.addFlashAttribute("succes",
                    "Encaissement confirmé, l'entrée de trésorerie a été créée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/caissier/paiement/detail/" + id;
    }

    private String formPaiement(Integer commandeId, boolean reste, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            Commandes commande = paiementService.verifierCommandeDuPointDeVente(commandeId, pdvCode(employe));
            Paiement paiement = paiementService.findByCommandeId(commandeId);
            BigDecimal montantRestant = paiementService.getMontantRestant(commande);

            PaiementFormDto dto = new PaiementFormDto();
            dto.setCommandeId(commandeId);
            dto.getLignes().add(new PaiementLigneDto(TypePayement.Espece, montantRestant));

            model.addAttribute("employe", employe);
            model.addAttribute("commande", commande);
            model.addAttribute("paiement", paiement);
            model.addAttribute("fillesExistantes", paiementService.findFilles(paiement));
            model.addAttribute("paiementFormDto", dto);
            model.addAttribute("typesPaiement", TypePayement.values());
            model.addAttribute("numerosTransfert", paiementService.getNumerosTransfertMobileMoney());
            model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(commande));
            model.addAttribute("montantPaye", paiementService.getMontantPaye(paiement));
            model.addAttribute("montantRestant", montantRestant);
            model.addAttribute("modeReste", reste);
            return "back/caissier/paiementForm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/commandes";
        }
    }

    private String enregistrerPaiement(Integer commandeId, PaiementFormDto form, HttpSession session,
            RedirectAttributes redirectAttributes, String vue) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            Paiement paiement = paiementService.creerPaiementCaissier(commandeId, form.getLignes(), pdvCode(employe));
            redirectAttributes.addFlashAttribute("succes", paiementService.aDesLignesEnAttente(paiement)
                    ? "Paiement enregistré. Il reste à confirmer pour être encaissé."
                    : "Paiement enregistré avec succès.");
            return "redirect:/caissier/paiement/detail/" + paiement.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caissier/paiement/" + vue + "/" + commandeId;
        }
    }

    @GetMapping("/paiement/detail/{id}")
    public String detailPaiement(@PathVariable Integer id, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        Paiement paiement = paiementService.findById(id);
        if (paiement == null || paiement.getCommande() == null
                || paiement.getCommande().getPointDeVenteCreateur() == null
                || !pdvCode(employe).equals(paiement.getCommande().getPointDeVenteCreateur().getCode())) {
            redirectAttributes.addFlashAttribute("error",
                    "Ce paiement n'existe pas ou n'appartient pas à votre point de vente.");
            return "redirect:/caissier/paiements";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("paiement", paiement);
        model.addAttribute("filles", paiementService.findFilles(paiement));
        model.addAttribute("montantTotal", paiementService.getMontantTotalCommande(paiement.getCommande()));
        model.addAttribute("montantPaye", paiementService.getMontantPaye(paiement));
        model.addAttribute("montantRestant", paiementService.getMontantRestant(paiement.getCommande()));
        model.addAttribute("montantEnAttente", paiementService.getMontantEnAttente(paiement));
        model.addAttribute("paiementService", paiementService);
        return "back/caissier/paiementDetail";
    }

    @GetMapping("/stock")
    public String stock(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        // Quantités disponibles limitées au point de vente du caissier.
        model.addAttribute("stocks", produitBackService.getStockLevels(null, null, pdvCode(employe)));
        return "back/caissier/stock";
    }

    @GetMapping("/mouvements")
    public String mouvements(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("mouvements",
                employe.getPointDeVente() != null
                        ? mvtStockRepository.findByPointDeVente(employe.getPointDeVente())
                        : new ArrayList<>());
        return "back/caissier/mouvements";
    }

    @GetMapping("/commande/new")
    public String nouvelleCommande(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        CommandeBackFormDto dto = new CommandeBackFormDto();
        dto.getDetailsCommande().add(new DetailCommandeBackDto());
        model.addAttribute("commandeBackFormDto", dto);
        remplirFormulaireCommande(model, employe);
        return "back/caissier/commandeCreate";
    }

    /** Options communes au formulaire de commande du caissier. */
    private void remplirFormulaireCommande(Model model, Employes employe) {
        model.addAttribute("employe", employe);
        model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
    }

    @PostMapping("/commande/save")
    public String saveCommande(@Valid @ModelAttribute("commandeBackFormDto") CommandeBackFormDto form,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        if ("Livraison_Domicile".equalsIgnoreCase(form.getModeReception())) {
            if (form.getAddress() == null || form.getAddress().trim().isEmpty()) {
                bindingResult.rejectValue("address", "error.address",
                        "L'adresse est obligatoire pour une livraison à domicile.");
            }
            if (form.getProvinceId() == null) {
                bindingResult.rejectValue("provinceId", "error.provinceId",
                        "La province de livraison est obligatoire.");
            }
        }
        // Le retrait se fait dans la boutique du caissier : on impose son point de
        // vente au lieu de le laisser choisir, sinon la commande serait rattachée à
        // une autre boutique et il ne pourrait plus l'encaisser.
        if ("Retrait_Boutique".equalsIgnoreCase(form.getModeReception())) {
            if (employe.getPointDeVente() == null) {
                bindingResult.rejectValue("pointDeVenteId", "error.pointDeVenteId",
                        "Aucun point de vente n'est rattaché à votre compte : le retrait en boutique est impossible.");
            } else {
                form.setPointDeVenteId(employe.getPointDeVente().getId());
            }
        }

        if (bindingResult.hasErrors()) {
            remplirFormulaireCommande(model, employe);
            return "back/caissier/commandeCreate";
        }

        try {
            form.setCodePointDeVendeCreateur(pdvCode(employe));
            commandesService.saveBackCommande(form);
            redirectAttributes.addFlashAttribute("succes", "Commande enregistrée avec succès.");
            return "redirect:/caissier/commandes";
        } catch (Exception e) {
            remplirFormulaireCommande(model, employe);
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "back/caissier/commandeCreate";
        }
    }

}
