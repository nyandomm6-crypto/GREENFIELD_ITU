package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import itu.greenField.model.Client;
import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/livreurs")
@RequiredArgsConstructor
public class DashboardLivreurController {
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

    @GetMapping("/mes-livraisons")
    public String mesLivraisons(HttpSession session, Model model) {
        Employes employe = (Employes) session.getAttribute("employe");

        if (employe == null || employe.getRole() != FRole.Livreur) {
            session.invalidate();
            return "redirect:/emp/login";
        }

        model.addAttribute("livreur", employe);
        return "back/livraison/mes-livraisons";
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
