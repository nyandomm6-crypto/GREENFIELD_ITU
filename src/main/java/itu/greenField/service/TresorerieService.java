package itu.greenField.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.NatureFlux;
import itu.greenField.model.Tresorerie;
import itu.greenField.repository.TresorerieRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TresorerieService {

    private final TresorerieRepository tresorerieRepository;

    public Page<Tresorerie> lister(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 10 : size);
        return tresorerieRepository.findAllByOrderByDateOperationDesc(pageable);
    }

    @Transactional
    public Tresorerie enregistrer(NatureFlux nature, BigDecimal montant, LocalDateTime dateOperation, String note) {
        if (nature == null) {
            throw new IllegalArgumentException("La nature du flux est obligatoire.");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif.");
        }

        Tresorerie mouvement = new Tresorerie();
        mouvement.setTypeMouvement(nature.getSens());
        mouvement.setMontant(montant);
        mouvement.setDateOperation(dateOperation != null ? dateOperation : LocalDateTime.now());

        String description = nature.getLibelle();
        if (note != null && !note.trim().isEmpty()) {
            description += " - " + note.trim();
        }
        mouvement.setDescription(description);

        return tresorerieRepository.save(mouvement);
    }
}
