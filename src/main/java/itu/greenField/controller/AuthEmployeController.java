package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Ancien point d'entrée employé. Depuis la mise en place du login unifié
 * (voir {@link AuthClientController}), il n'y a plus qu'un seul formulaire
 * de connexion sur « /login ». On conserve « /emp/login » uniquement comme
 * redirection, car de nombreux contrôleurs back-office y renvoient encore
 * quand la session employé a expiré.
 */
@Controller
@RequestMapping("/emp")
public class AuthEmployeController {

    @GetMapping("/login")
    public String accueil(@RequestParam(required = false) String redirect) {
        if (redirect == null || redirect.isBlank()) {
            return "redirect:/login";
        }
        // On propage le paramètre redirect vers le login unifié.
        String target = UriComponentsBuilder.fromPath("/login")
                .queryParam("redirect", redirect)
                .toUriString();
        return "redirect:" + target;
    }
}
