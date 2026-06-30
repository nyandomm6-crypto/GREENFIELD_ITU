package itu.GreenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaiementController {

    @GetMapping("/paiement/form/")
    public String showForm() {


        return "paiement/form_paiement";
    }

}
