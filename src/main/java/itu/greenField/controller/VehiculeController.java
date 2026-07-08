package itu.greenField.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itu.greenField.model.StatutVehicule;
import itu.greenField.model.Vehicule;
import itu.greenField.service.VehiculeService;

@Controller
@RequestMapping("/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @Autowired
    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public String listeVehicules(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) StatutVehicule statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Vehicule> vehiculesPage = vehiculeService.listerPage(motCle, date, statut, page, size);
        model.addAttribute("vehicules", vehiculesPage.getContent());
        model.addAttribute("page", vehiculesPage.getNumber());
        model.addAttribute("totalPages", vehiculesPage.getTotalPages());
        model.addAttribute("totalElements", vehiculesPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("motCle", motCle);
        model.addAttribute("date", date);
        model.addAttribute("statut", statut);
        model.addAttribute("statuts", StatutVehicule.values());
        return "front/vehicules/list";
    }

    // ==================== EXCEL ====================

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportVehicules() throws Exception {
        return excelResponse(vehiculeService.exportExcel(), "vehicules.xlsx");
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> templateVehicules() throws Exception {
        return excelResponse(vehiculeService.templateExcel(), "modele_vehicules.xlsx");
    }

    @GetMapping("/import")
    public String showImportForm() {
        return "front/vehicules/importExcel";
    }

    @PostMapping("/import")
    public String importVehicules(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty() || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un fichier Excel valide (.xlsx).");
            return "redirect:/vehicules/import";
        }
        try {
            int count = vehiculeService.importExcel(file.getInputStream());
            redirectAttributes.addFlashAttribute("success", count + " véhicule(s) importé(s) avec succès.");
            return "redirect:/vehicules";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation : " + e.getMessage());
            return "redirect:/vehicules/import";
        }
    }

    private ResponseEntity<byte[]> excelResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }

    @GetMapping("/ajouter")
    public String afficherFormulaireAjout(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("statuts", StatutVehicule.values());
        model.addAttribute("edit", false);
        return "front/vehicules/form";
    }

    @PostMapping("/ajouter")
    public String creerVehicule(@ModelAttribute Vehicule vehicule) {
        vehiculeService.ajouterVehicule(vehicule);
        return "redirect:/vehicules";
    }

    @GetMapping("/{id}")
    public String voirDetails(@PathVariable Integer id, Model model) {
        Vehicule vehicule = vehiculeService.obtenirParId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule introuvable"));
        model.addAttribute("vehicule", vehicule);
        return "front/vehicules/details";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model) {
        Vehicule vehicule = vehiculeService.obtenirParId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule introuvable"));
        model.addAttribute("vehicule", vehicule);
        model.addAttribute("statuts", StatutVehicule.values());
        model.addAttribute("edit", true);
        return "front/vehicules/form";
    }

    @PostMapping("/{id}/modifier")
    public String modifierVehicule(@PathVariable Integer id, @ModelAttribute Vehicule vehicule) {
        vehiculeService.modifierVehicule(id, vehicule);
        return "redirect:/vehicules";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerVehicule(@PathVariable Integer id) {
        vehiculeService.supprimerVehicule(id);
        return "redirect:/vehicules";
    }
}
