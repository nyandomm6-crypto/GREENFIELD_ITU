package itu.greenfield.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import itu.greenfield.dto.FraisLivraisonFilterDto;
import itu.greenfield.dto.FraisLivraisonFormDto;
import itu.greenfield.filtre.CalculOption;
import itu.greenfield.filtre.FiltreNombreBackFraisOption;
import itu.greenfield.model.FraisLivraison;
import itu.greenfield.service.FraisLivraisonService;
import itu.greenfield.service.ProvinceLivraisonService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/frais")
public class FraisLivraisonController {
    private final FraisLivraisonService fraisLivraisonService;
    private final ProvinceLivraisonService provinceLivraisonService;

    public FraisLivraisonController(FraisLivraisonService fraisLivraisonService,
            ProvinceLivraisonService provinceLivraisonService) {
        this.fraisLivraisonService = fraisLivraisonService;
        this.provinceLivraisonService = provinceLivraisonService;
    }

    @GetMapping("/form/new")
    public ModelAndView showCreateForm() {
        ModelAndView mv = new ModelAndView("back/frais/formFrais");

        // Initialisation d'un DTO vide
        FraisLivraisonFormDto dto = new FraisLivraisonFormDto();

        mv.addObject("fraisFormDto", dto);
        mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
        return mv;
    }

    @GetMapping("/form/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("back/frais/formFrais");
        FraisLivraison frais = null;

        try {
            frais = fraisLivraisonService.getFraisById(id);
            FraisLivraisonFormDto dto = new FraisLivraisonFormDto(frais);

            mv.addObject("fraisFormDto", dto);
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());

        } catch (Exception e) {
            mv.addObject("alert", "Le frais de livraison #" + id + " ne peut pas être modifié. " + e.getMessage());

            mv.setViewName("back/frais/listeFrais");

            FraisLivraisonFilterDto filter = new FraisLivraisonFilterDto();
            generateListFraisModel(mv, filter);
        }

        return mv;
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("fraisFormDto") FraisLivraisonFormDto form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            return "back/frais/formFrais";
        }

        try {
            FraisLivraison frais = fraisLivraisonService.saveOrUpdateFrais(form);

            redirectAttributes.addFlashAttribute("succes",
                    "Le frais de livraison a été sauvegardé avec succès. #" + frais.getId());

            return "redirect:/frais/list";

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            model.addAttribute("alert", "Erreur lors de la sauvegarde : " + e.getMessage());

            return "back/frais/formFrais";
        }
    }

    @GetMapping({ "/", "", "/list" })
    public ModelAndView listFrais() {
        ModelAndView mv = new ModelAndView("back/frais/listeFrais");
        FraisLivraisonFilterDto filter = new FraisLivraisonFilterDto();
        generateListFraisModel(mv, filter);
        return mv;
    }

    @PostMapping({ "/", "", "/list" })
    public ModelAndView filteredListFrais(
            @ModelAttribute("fraisFilterDto") FraisLivraisonFilterDto filter) {

        ModelAndView mv = new ModelAndView("back/frais/listeFrais");
        generateListFraisModel(mv, filter);
        return mv;
    }

    private void generateListFraisModel(ModelAndView mv, FraisLivraisonFilterDto filter) {
        Page<FraisLivraison> fraisPage = fraisLivraisonService.findWithDynamicFilters(filter);

        if (mv != null) {
            // Données de la table et pagination
            mv.addObject("fraisLivraisonList", fraisPage.getContent());
            mv.addObject("totalPages", fraisPage.getTotalPages());
            mv.addObject("hasPrevious", fraisPage.hasPrevious());
            mv.addObject("hasNext", fraisPage.hasNext());

            // On réinjecte le DTO de filtrage pour conserver les états saisis dans l'UI
            mv.addObject("fraisFilterDto", filter);

            // Options pour alimenter les éléments HTML (Selects, Multi-choices)
            mv.addObject("provinceLivraisonOptions", provinceLivraisonService.getAllProvinces());
            mv.addObject("calculOptions", CalculOption.values()); // Ex: "=", "<=", ">="
            mv.addObject("filtreNombreOptions", FiltreNombreBackFraisOption.values()); // Ex: poidsreference, montant
        }
    }

}
