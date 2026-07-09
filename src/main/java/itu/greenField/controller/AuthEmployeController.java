package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.service.EmployesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/emp")
@RequiredArgsConstructor
public class AuthEmployeController {

    private final EmployesService employesService;

    @GetMapping("/login")
    public String accueil(@RequestParam(required = false) String redirect,
            HttpSession session,
            Model model) {
        model.addAttribute("redirect", redirect);
        return "back/auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Employes employe = employesService.findByEmail(email);

        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Email tsy misy");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/emp/login";
        }

        if (!employe.getMotdepasse().equals(motDePasse)) {
            redirectAttributes.addFlashAttribute("error", "mot de passe diso");
            redirectAttributes.addFlashAttribute("redirect", redirect);
            return "redirect:/emp/login";
        }

        if (employe.getRole().equals(FRole.Livreur)) {
            session.setAttribute("employe", employesService.getById(employe.getId()));
            return "redirect:/livreurs/dashboard";
        }

        if (employe.getRole().equals(FRole.Caissier)) {
            session.setAttribute("employe", employesService.getById(employe.getId()));
            if (redirect != null && !redirect.isBlank() && redirect.startsWith("/caissier")) {
                return "redirect:" + redirect;
            }
            return "redirect:/caissier/dashboard";
        }

        if (employe.getRole().equals(FRole.Administrateur)) {
            session.setAttribute("employe", employesService.getById(employe.getId()));
            return "redirect:/commandes/list";
        }

        session.setAttribute("employe", employesService.getById(employe.getId()));
        return "redirect:/emp/login";
    }
}
