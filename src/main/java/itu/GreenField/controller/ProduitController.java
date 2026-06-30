package itu.greenfield.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import itu.greenfield.service.ProduitService;

@Controller
@RequestMapping("/produits")
public class ProduitController {
    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping(value = "/{matricule}/detail-json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getProduit(@PathVariable String matricule) {
        return produitService.produitToJson(produitService.findProduitByMatricule(matricule));
    }

    
}
