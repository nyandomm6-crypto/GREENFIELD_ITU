package itu.greenField.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Temoignage;
import itu.greenField.repository.TemoignageRepository;

@Service
public class TemoignageService {

    private final TemoignageRepository temoignageRepository;

    public TemoignageService(TemoignageRepository temoignageRepository) {
        this.temoignageRepository = temoignageRepository;
    }

    public List<Temoignage> findAllActifs() {
        List<Temoignage> temoignages = temoignageRepository.findByIsActifTrueOrderByIdDesc();
        if (temoignages.isEmpty()) {
            seedDefaultTestimonials();
            return temoignageRepository.findByIsActifTrueOrderByIdDesc();
        }
        return temoignages;
    }

    @Transactional
    public Temoignage enregistrer(Temoignage temoignage) {
        return temoignageRepository.save(temoignage);
    }

    @Transactional
    public void seedDefaultTestimonials() {
        if (temoignageRepository.count() > 0) {
            return;
        }

        Temoignage premier = new Temoignage();
        premier.setNom("Mialy Rakoto");
        premier.setPoste("Client premium");
        premier.setMessage("Le service est rapide, le cadre est agréable et les produits sont vraiment de qualité.");
        premier.setIsActif(true);

        Temoignage second = new Temoignage();
        second.setNom("Nirina Andriamiarina");
        second.setPoste("Mère de famille");
        second.setMessage("J’ai trouvé exactement ce qu’il me fallait pour mes plantations. Merci GreenField !");
        second.setIsActif(true);

        temoignageRepository.saveAll(List.of(premier, second));
    }
}
