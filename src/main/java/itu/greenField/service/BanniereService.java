package itu.greenField.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import itu.greenField.model.Banniere;
import itu.greenField.repository.BanniereRepository;
import itu.greenField.service.FileStorageService;

@Service
public class BanniereService {

    private final BanniereRepository banniereRepository;
    private final FileStorageService fileStorageService;

    public BanniereService(BanniereRepository banniereRepository, FileStorageService fileStorageService) {
        this.banniereRepository = banniereRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Banniere> findAll() {
        seedDefaultBannieresIfEmpty();
        return banniereRepository.findAll();
    }

    public Banniere findById(Long id) {
        return banniereRepository.findById(id).orElse(null);
    }

    @Transactional
    public Banniere save(Banniere banniere) {
        return banniereRepository.save(banniere);
    }

    @Transactional
    public Banniere saveWithImage(Banniere banniere, MultipartFile image) {
        Banniere saved = banniereRepository.save(banniere);
        if (image != null && !image.isEmpty()) {
            String path = fileStorageService.store(image, "bannieres", "ban" + saved.getId());
            saved.setImagePath(path);
            saved = banniereRepository.save(saved);
        }
        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        banniereRepository.deleteById(id);
    }

    @Transactional
    public void seedDefaultBannieresIfEmpty() {
        if (banniereRepository.count() > 0) {
            return;
        }

        Banniere banniere = new Banniere();
        banniere.setTitre("Fruits exotiques frais");
        banniere.setSousTitre("dans notre boutique");
        banniere.setDescription(
                "Découvrez notre sélection de fruits exotiques frais, directement issus de nos producteurs locaux.");
        banniere.setImagePath("img/baner-1.png");
        banniere.setLien("#");
        banniere.setBtnTexte("ACHETER");
        banniere.setPromoNombre("1");
        banniere.setPromoPrix("50 000 Ar");
        banniere.setPromoUnite("kg");

        banniereRepository.save(banniere);
    }
}
