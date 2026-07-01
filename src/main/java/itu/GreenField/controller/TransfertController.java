package itu.GreenField.controller;

import itu.GreenField.dto.CreerTransfertRequest;
import itu.GreenField.dto.DemandeTransfertRequest;
import itu.GreenField.dto.TransfertDetailResponse;
import itu.GreenField.model.DemandeStock;
import itu.GreenField.service.TransfertService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transferts")
public class TransfertController {

    private final TransfertService transfertService;

    // Constructeur manuel (remplace @RequiredArgsConstructor)
    public TransfertController(TransfertService transfertService) {
        this.transfertService = transfertService;
    }

    @PostMapping("/demande")
    public ResponseEntity<DemandeStock> demanderTransfert(@RequestBody DemandeTransfertRequest req) {
        return ResponseEntity.ok(transfertService.demandeTransfert(req));
    }

    @PostMapping
    public ResponseEntity<TransfertDetailResponse> creerTransfert(@RequestBody CreerTransfertRequest req) {
        return ResponseEntity.ok(transfertService.creerTransfert(req));
    }

    @GetMapping
    public ResponseEntity<List<TransfertDetailResponse>> listerTransferts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) String codePointDeVente) {
        return ResponseEntity.ok(transfertService.listerTransferts(dateDebut, dateFin, codePointDeVente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransfertDetailResponse> detailTransfert(@PathVariable Long id) {
        return ResponseEntity.ok(transfertService.detailTransfert(id));
    }
}