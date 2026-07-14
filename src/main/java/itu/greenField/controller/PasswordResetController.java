package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.service.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/mot-de-passe-oublie")
    public String forgotPasswordForm() {
        return "front/auth/forgot-password";
    }

    @PostMapping("/mot-de-passe-oublie")
    public String requestReset(@RequestParam String email, RedirectAttributes redirectAttributes) {
        boolean sent = passwordResetService.requestReset(email);
        if (!sent) {
            redirectAttributes.addFlashAttribute("error", "Aucun compte trouvé pour cet email.");
            return "redirect:/mot-de-passe-oublie";
        }

        redirectAttributes.addFlashAttribute("success", "Un code de vérification a été envoyé à votre adresse e-mail.");
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/reinitialiser-mot-de-passe";
    }

    @GetMapping("/reinitialiser-mot-de-passe")
    public String resetPasswordForm(@RequestParam(required = false) String email, Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "front/auth/reset-password";
    }

    @PostMapping("/reinitialiser-mot-de-passe")
    public String resetPassword(@RequestParam String email,
            @RequestParam String code,
            @RequestParam String motDePasse,
            RedirectAttributes redirectAttributes) {
        boolean ok = passwordResetService.resetPassword(email, code, motDePasse);
        if (!ok) {
            redirectAttributes.addFlashAttribute("error", "Code invalide ou expiré.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reinitialiser-mot-de-passe";
        }

        redirectAttributes.addFlashAttribute("success", "Votre mot de passe a été réinitialisé avec succès.");
        return "redirect:/login";
    }
}
