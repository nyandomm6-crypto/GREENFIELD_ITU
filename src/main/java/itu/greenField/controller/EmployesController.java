package itu.greenField.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.Employes;
import itu.greenField.model.FRole;
import itu.greenField.repository.PointDeVenteRepository;
import itu.greenField.service.EmployesService;

@Controller
@RequestMapping("/employes")
public class EmployesController {
    private final EmployesService employesService;
    private final PointDeVenteRepository pointDeVenteRepository;

    public EmployesController(
            EmployesService employesService,
            PointDeVenteRepository pointDeVenteRepository) {
        this.employesService = employesService;
        this.pointDeVenteRepository = pointDeVenteRepository;
    }

    @GetMapping({"","/"})
    public String liste(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) FRole role,
            Model model) {
        remplirListe(model, true, motCle, date, role);
        return "front/employes/list";
    }

    @GetMapping("/archives")
    public String archives(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) FRole role,
            Model model) {
        remplirListe(model, false, motCle, date, role);
        return "front/employes/list";
    }

    @GetMapping("/new")
    public String formulaireAjout(Model model) {
        Employes employe = new Employes();
        employe.setDate(LocalDate.now());
        remplirFormulaire(model, employe, null, "/employes", "Ajouter employe", true);
        return "front/employes/form";
    }

    @PostMapping
    public String create(
            @ModelAttribute Employes employe,
            @RequestParam(required = false) String codePointDeVente,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            Employes employeCree = employesService.create(employe, codePointDeVente);
            redirectAttributes.addFlashAttribute("success", "Employe ajoute avec succes.");
            return "redirect:/employes/" + employeCree.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            remplirFormulaire(model, employe, codePointDeVente, "/employes", "Ajouter employe", true);
            return "front/employes/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("employe", employesService.getById(id));
        return "front/employes/detail";
    }

    @GetMapping("/{id}/edit")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        Employes employe = employesService.getById(id);
        String code = employe.getPointDeVente() == null ? null : employe.getPointDeVente().getCode();
        remplirFormulaire(model, employe, code, "/employes/" + id, "Modifier employe", false);
        return "front/employes/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Integer id,
            @ModelAttribute Employes employe,
            @RequestParam(required = false) String codePointDeVente,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            employesService.update(id, employe, codePointDeVente);
            redirectAttributes.addFlashAttribute("success", "Employe modifie avec succes.");
            return "redirect:/employes/" + id;
        } catch (IllegalArgumentException e) {
            employe.setId(id);
            model.addAttribute("error", e.getMessage());
            remplirFormulaire(model, employe, codePointDeVente, "/employes/" + id, "Modifier employe", false);
            return "front/employes/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        employesService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Employe archive avec succes.");
        return "redirect:/employes";
    }

    private void remplirListe(
            Model model,
            Boolean estActif,
            String motCle,
            LocalDate date,
            FRole role) {
        model.addAttribute("employes", employesService.filtrer(estActif, motCle, date, role));
        model.addAttribute("roles", FRole.values());
        model.addAttribute("motCle", motCle);
        model.addAttribute("date", date);
        model.addAttribute("role", role);
        model.addAttribute("estActif", estActif);
        model.addAttribute("titre", estActif ? "Liste employe" : "Liste archive employe");
    }

    private void remplirFormulaire(
            Model model,
            Employes employe,
            String codePointDeVente,
            String action,
            String titre,
            boolean creation) {
        model.addAttribute("employe", employe);
        model.addAttribute("roles", FRole.values());
        model.addAttribute("pointsDeVente", pointDeVenteRepository.findAllByOrderByNomAsc());
        model.addAttribute("codePointDeVente", codePointDeVente);
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
        model.addAttribute("creation", creation);
    }
}
