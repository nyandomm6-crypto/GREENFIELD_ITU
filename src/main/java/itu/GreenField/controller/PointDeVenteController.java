package itu.GreenField.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.GreenField.model.Employes;
import itu.GreenField.model.MvtStock;
import itu.GreenField.model.MvtStockFille;
import itu.GreenField.model.PointDeVente;
import itu.GreenField.model.Produit;
import itu.GreenField.model.TypeMvt;
import itu.GreenField.repository.EmployesRepository;
import itu.GreenField.repository.MvtStockFilleRepository;
import itu.GreenField.repository.MvtStockRepository;
import itu.GreenField.repository.PointDeVenteRepository;
import itu.GreenField.repository.ProduitRepository;
import itu.GreenField.repository.CategorieProduitRepository;
import itu.GreenField.service.PointDeVenteService;

@Controller
@RequestMapping("/pointdevente")
public class PointDeVenteController {

    private PointDeVenteService pointDeVenteService;
    private PointDeVenteRepository pointDeVenteRepository;
    private EmployesRepository employesRepository;
    private MvtStockRepository mvtStockRepository;
    private MvtStockFilleRepository mvtStockFilleRepository;
    private ProduitRepository produitRepository;
    private CategorieProduitRepository categorieProduitRepository;

    public PointDeVenteController(PointDeVenteService pointDeVenteService,
            PointDeVenteRepository pointDeVenteRepository,
            EmployesRepository employesRepository,
            MvtStockRepository mvtStockRepository,
            MvtStockFilleRepository mvtStockFilleRepository,
            ProduitRepository produitRepository,
            CategorieProduitRepository categorieProduitRepository) {
        this.pointDeVenteService = pointDeVenteService;
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.employesRepository = employesRepository;
        this.mvtStockRepository = mvtStockRepository;
        this.mvtStockFilleRepository = mvtStockFilleRepository;
        this.produitRepository = produitRepository;
        this.categorieProduitRepository = categorieProduitRepository;
    }

    // Liste des points de vente
    @GetMapping
    public String listePointDeVente(Model model, @RequestParam(required = false) String code) {
        List<PointDeVente> pointDeVentes;
        if (code != null && !code.isEmpty()) {
            pointDeVentes = pointDeVenteRepository.findByCodeContainingIgnoreCase(code);
        } else {
            pointDeVentes = pointDeVenteService.getAllPointDeVente();
        }
        model.addAttribute("pointDeVentes", pointDeVentes);
        model.addAttribute("code", code);
        return "pointdevente/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("pointDeVente", new PointDeVente());
        return "pointdevente/formulaire";
    }

    // Créer un point de vente
    @PostMapping("/ajouter")
    public String creerPointDeVente(@ModelAttribute PointDeVente pointDeVente, RedirectAttributes redirectAttributes) {
        pointDeVenteService.createPointDeVente(pointDeVente);
        redirectAttributes.addFlashAttribute("success", "Point de vente créé avec succès");
        return "redirect:/pointdevente";
    }

    // Voir détail
    @GetMapping("/{id}")
    public String voirDetail(@PathVariable Integer id, Model model) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            return "pointdevente/detail";
        }
        return "redirect:/pointdevente";
    }

    // Formulaire de modification (pré-rempli)
    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Integer id, Model model) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            return "pointdevente/formulaire";
        }
        return "redirect:/pointdevente";
    }

    // Modifier un point de vente
    @PostMapping("/{id}/modifier")
    public String modifierPointDeVente(@PathVariable Integer id, @ModelAttribute PointDeVente pointDeVente,
            RedirectAttributes redirectAttributes) {
        pointDeVenteService.updatePointDeVente(id, pointDeVente);
        redirectAttributes.addFlashAttribute("success", "Point de vente modifié avec succès");
        return "redirect:/pointdevente";
    }

    // Supprimer un point de vente
    @PostMapping("/{id}/supprimer")
    public String supprimerPointDeVente(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        pointDeVenteService.deletePointDeVente(id);
        redirectAttributes.addFlashAttribute("success", "Point de vente supprimé avec succès");
        return "redirect:/pointdevente";
    }

    // Stock produit dans point de vente
    @GetMapping("/{id}/stock")
    public String stockProduit(@PathVariable Integer id, Model model,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String produit) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            model.addAttribute("categorie", categorie);
            model.addAttribute("produit", produit);
            model.addAttribute("categories", categorieProduitRepository.findAll());
            // Récupérer les mouvements de stock de type Entree_Boutique pour ce point de vente
            List<MvtStock> mouvements = mvtStockRepository.findByPointDeVenteAndTypeMouvement(
                    pointDeVente.get(), TypeMvt.Entree_Boutique);
            model.addAttribute("mouvements", mouvements);
            return "pointdevente/stock";
        }
        return "redirect:/pointdevente";
    }

    // Formulaire ajouter produit au point de vente
    @GetMapping("/{id}/ajouter-produit")
    public String formulaireAjouterProduit(@PathVariable Integer id, Model model) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            model.addAttribute("produits", produitRepository.findAll());
            return "pointdevente/ajouter-produit";
        }
        return "redirect:/pointdevente";
    }

    // Ajouter produit au point de vente (créer mouvement entrée)
    @PostMapping("/{id}/ajouter-produit")
    public String ajouterProduit(@PathVariable Integer id,
            @RequestParam Integer idProduit,
            @RequestParam Integer quantite,
            RedirectAttributes redirectAttributes) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        Optional<Produit> produit = produitRepository.findById(idProduit);
        
        if (pointDeVente.isPresent() && produit.isPresent()) {
            // Créer mouvement de stock
            MvtStock mvtStock = new MvtStock();
            mvtStock.setPointDeVente(pointDeVente.get());
            mvtStock.setTypeMouvement(TypeMvt.Entree_Boutique);
            mvtStock.setDateMvt(LocalDateTime.now());
            mvtStockRepository.save(mvtStock);
            
            // Créer mouvement fille
            MvtStockFille mvtStockFille = new MvtStockFille();
            mvtStockFille.setMvtStock(mvtStock);
            mvtStockFille.setProduit(produit.get());
            mvtStockFille.setQuantite(quantite);
            mvtStockFilleRepository.save(mvtStockFille);
            
            redirectAttributes.addFlashAttribute("success", "Produit ajouté au point de vente avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout du produit");
        }
        
        return "redirect:/pointdevente/" + id + "/stock";
    }

    // Liste employe point de vente
    @GetMapping("/{id}/employes")
    public String listeEmploye(@PathVariable Integer id, Model model,
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String role) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            model.addAttribute("motCle", motCle);
            model.addAttribute("role", role);
            
            List<Employes> employes;
            if (motCle != null && !motCle.isEmpty() && role != null && !role.isEmpty()) {
                employes = employesRepository.findByPointDeVenteAndNomContainingIgnoreCaseAndRole(
                        pointDeVente.get(), motCle, itu.GreenField.model.FRole.valueOf(role));
            } else if (motCle != null && !motCle.isEmpty()) {
                employes = employesRepository.findByPointDeVenteAndNomContainingIgnoreCase(
                        pointDeVente.get(), motCle);
            } else if (role != null && !role.isEmpty()) {
                employes = employesRepository.findByPointDeVenteAndRole(
                        pointDeVente.get(), itu.GreenField.model.FRole.valueOf(role));
            } else {
                employes = employesRepository.findByPointDeVente(pointDeVente.get());
            }
            
            model.addAttribute("employes", employes);
            return "pointdevente/employes";
        }
        return "redirect:/pointdevente";
    }

    // Liste mouvement point de vente
    @GetMapping("/{id}/mouvements")
    public String listeMouvement(@PathVariable Integer id, Model model,
            @RequestParam(required = false) TypeMvt typeMvt,
            @RequestParam(required = false) LocalDateTime dateDebut,
            @RequestParam(required = false) LocalDateTime dateFin,
            @RequestParam(required = false) String produit) {
        Optional<PointDeVente> pointDeVente = pointDeVenteService.getPointDeVenteById(id);
        if (pointDeVente.isPresent()) {
            model.addAttribute("pointDeVente", pointDeVente.get());
            model.addAttribute("typeMvt", typeMvt);
            model.addAttribute("dateDebut", dateDebut);
            model.addAttribute("dateFin", dateFin);
            model.addAttribute("produit", produit);
            
            List<MvtStock> mouvements = mvtStockRepository.findByPointDeVente(pointDeVente.get());
            
            // Filtrer par type de mouvement
            if (typeMvt != null) {
                mouvements = mouvements.stream()
                    .filter(m -> m.getTypeMouvement().equals(typeMvt))
                    .toList();
            }
            
            // Filtrer par nom de produit
            if (produit != null && !produit.isEmpty()) {
                mouvements = mouvements.stream()
                    .filter(m -> m.getMvtStockFilles() != null && 
                        m.getMvtStockFilles().stream()
                            .anyMatch(f -> f.getProduit() != null && 
                                f.getProduit().getNom() != null && 
                                f.getProduit().getNom().toLowerCase().contains(produit.toLowerCase())))
                    .toList();
            }

            // Filtrer par date de début
            if (dateDebut != null) {
                mouvements = mouvements.stream()
                    .filter(m -> m.getDateMvt() != null && m.getDateMvt().compareTo(dateDebut) >= 0)
                    .toList();
            }

            // Filtrer par date de fin
            if (dateFin != null) {
                mouvements = mouvements.stream()
                    .filter(m -> m.getDateMvt() != null && m.getDateMvt().compareTo(dateFin) <= 0)
                    .toList();
            }

            model.addAttribute("mouvements", mouvements);
            
            return "pointdevente/mouvements";
        }
        return "redirect:/pointdevente";
    }
}
