package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import itu.greenField.model.Client;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.model.Livraison;
import itu.greenField.service.LivraisonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/livreurs")
@RequiredArgsConstructor
public class DashboardLivreurController {

    private final LivraisonService livraisonService;

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

}
