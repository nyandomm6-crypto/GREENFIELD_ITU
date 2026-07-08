package itu.greenField.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.NatureFlux;
import itu.greenField.model.Tresorerie;
import itu.greenField.service.TresorerieService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/back/tresorerie")
public class TresorerieController {

    private final TresorerieService tresorerieService;

    @GetMapping({ "", "/", "/list" })
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Tresorerie> mouvementsPage = tresorerieService.lister(page, size);
        model.addAttribute("mouvements", mouvementsPage.getContent());
        model.addAttribute("page", mouvementsPage.getNumber());
        model.addAttribute("totalPages", mouvementsPage.getTotalPages());
        model.addAttribute("totalElements", mouvementsPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("natures", NatureFlux.values());
        return "back/tresorerie/list";
    }

    @PostMapping("/nouveau")
    public String creer(@RequestParam NatureFlux nature,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateOperation,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        try {
            tresorerieService.enregistrer(nature, montant, dateOperation, note);
            redirectAttributes.addFlashAttribute("success", "Mouvement de trésorerie enregistré avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/back/tresorerie";
    }
}
