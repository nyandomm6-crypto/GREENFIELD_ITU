package itu.GreenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaiementController {

    @GetMapping("/paiement/form/")
    public String showForm(Model model) {

        return "paiement/form_paiement";
    }

}
