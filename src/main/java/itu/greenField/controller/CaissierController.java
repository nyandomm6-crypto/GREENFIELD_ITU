package itu.greenField.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.dto.CommandeBackFormDto;
import itu.greenField.dto.DetailCommandeBackDto;
import itu.greenField.dto.PaiementLigneDto;
import itu.greenField.model.Commandes;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.ModeReception;
import itu.greenField.model.Paiement;
import itu.greenField.model.TypePayement;
import itu.greenField.service.AuthGuard;
import itu.greenField.service.CommandesService;
import itu.greenField.service.ProduitBackService;
import itu.greenField.service.ProduitService;
import itu.greenField.service.PaiementService;
import itu.greenField.service.PointDeVenteService;
import itu.greenField.service.ProvinceLivraisonService;
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
        model.addAttribute("commandes", commandesService.findByPointDeVenteRetrait(code));
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
    public String commandes(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("commandes", commandesService.findByPointDeVenteRetrait(pdvCode(employe)));
        return "back/caissier/commandes";
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
        if (commande == null || commande.getPointDeVenteRetrait() == null
                || !pdvCode(employe).equals(commande.getPointDeVenteRetrait().getCode())) {
            redirectAttributes.addFlashAttribute("error",
                    "Cette commande n'existe pas ou n'appartient pas à votre point de vente.");
            return "redirect:/caissier/commandes";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("commande", commande);
        model.addAttribute("reste", paiementService.getMontantRestant(commande));
        model.addAttribute("dejaPaye", paiementService.findByCommandeId(commande.getId()) != null);
        model.addAttribute("typePayements", TypePayement.values());
        return "back/caissier/commandeDetail";
    }

    @GetMapping("/paiements")
    public String paiements(HttpSession session, Model model) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("paiements", paiementService.findByPointDeVente(pdvCode(employe)));
        model.addAttribute("paiementService", paiementService);
        return "back/caissier/paiements";
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
                || paiement.getCommande().getPointDeVenteRetrait() == null
                || !pdvCode(employe).equals(paiement.getCommande().getPointDeVenteRetrait().getCode())) {
            redirectAttributes.addFlashAttribute("error",
                    "Ce paiement n'existe pas ou n'appartient pas à votre point de vente.");
            return "redirect:/caissier/paiements";
        }
        model.addAttribute("employe", employe);
        model.addAttribute("paiement", paiement);
        model.addAttribute("filles", paiementService.findFilles(paiement));
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
        model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
        return "back/caissier/commandeCreate";
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
        if ("Retrait_Boutique".equalsIgnoreCase(form.getModeReception()) && form.getPointDeVenteId() == null) {
            bindingResult.rejectValue("pointDeVenteId", "error.pointDeVenteId",
                    "Le point de vente de retrait est obligatoire.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
            return "back/caissier/commandeCreate";
        }

        try {
            commandesService.saveBackCommande(form);
            redirectAttributes.addFlashAttribute("succes", "Commande enregistrée avec succès.");
            return "redirect:/caissier/commandes";
        } catch (Exception e) {
            model.addAttribute("produits", produitService.getAllProduits());
            model.addAttribute("modeReceptionOptions", ModeReception.getAllModeReception());
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("pointDeVenteOptions", pointDeVenteService.getAll());
            model.addAttribute("globalError", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "back/caissier/commandeCreate";
        }
    }

    /**
     * Création d'un paiement par le caissier (côté back-end). Le montant est
     * réglé pour la totalité du reste à payer de la commande.
     */
    @PostMapping("/paiement/save")
    public String savePaiement(@RequestParam Integer commandeId,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) TypePayement typePayement,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Employes employe = requireCaissier(session);
        if (employe == null) {
            session.invalidate();
            return "redirect:/emp/login";
        }
        try {
            PaiementLigneDto ligne = new PaiementLigneDto(
                    typePayement != null ? typePayement : TypePayement.Espece, montant);
            List<PaiementLigneDto> lignes = new ArrayList<>();
            lignes.add(ligne);
            paiementService.creerPaiementCaissier(commandeId, lignes, pdvCode(employe));
            redirectAttributes.addFlashAttribute("succes", "Paiement enregistré avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du paiement : " + e.getMessage());
        }
        return "redirect:/caissier/paiements";
    }

}
