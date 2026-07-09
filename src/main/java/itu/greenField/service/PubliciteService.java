package itu.greenField.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import itu.greenField.model.Publicite;
import itu.greenField.repository.PubliciteRepository;
import itu.greenField.service.FileStorageService;

@Service
public class PubliciteService {

        private final PubliciteRepository publiciteRepository;
        private final FileStorageService fileStorageService;

        public PubliciteService(PubliciteRepository publiciteRepository, FileStorageService fileStorageService) {
                this.publiciteRepository = publiciteRepository;
                this.fileStorageService = fileStorageService;
        }

        public List<Publicite> findAll() {
                seedDefaultPublicitesIfEmpty();
                return publiciteRepository.findAll();
        }

        public Publicite findById(Long id) {
                return publiciteRepository.findById(id).orElse(null);
        }

        @Transactional
        public Publicite save(Publicite publicite) {
                return publiciteRepository.save(publicite);
        }

        @Transactional
        public Publicite saveWithImage(Publicite publicite, MultipartFile image) {
                Publicite saved = publiciteRepository.save(publicite);
                if (image != null && !image.isEmpty()) {
                        String path = fileStorageService.store(image, "publicites", "pub" + saved.getId());
                        saved.setImagePath(path);
                        saved = publiciteRepository.save(saved);
                }
                return saved;
        }

        @Transactional
        public void deleteById(Long id) {
                publiciteRepository.deleteById(id);
        }

        @Transactional
        public void seedDefaultPublicitesIfEmpty() {
                if (publiciteRepository.count() > 0) {
                        return;
                }

                List<Publicite> publicites = new ArrayList<>();

                Publicite pub1 = new Publicite();
                pub1.setImagePath("img/featur-1.jpg");
                pub1.setTitre("Fresh Apples");
                pub1.setSousTitre("20% OFF");
                pub1.setLien("#");
                pub1.setClassDiv("service-item bg-secondary rounded border border-secondary");
                pub1.setClassContent("service-content bg-primary text-center p-4 rounded");
                pub1.setClassTitre("text-white");
                publicites.add(pub1);

                Publicite pub2 = new Publicite();
                pub2.setImagePath("img/featur-2.jpg");
                pub2.setTitre("Tasty Fruits");
                pub2.setSousTitre("Free delivery");
                pub2.setLien("#");
                pub2.setClassDiv("service-item bg-dark rounded border border-dark");
                pub2.setClassContent("service-content bg-light text-center p-4 rounded");
                pub2.setClassTitre("text-primary");
                publicites.add(pub2);

                Publicite pub3 = new Publicite();
                pub3.setImagePath("img/featur-3.jpg");
                pub3.setTitre("Exotic Vegetable");
                pub3.setSousTitre("Discount 30$");
                pub3.setLien("#");
                pub3.setClassDiv("service-item bg-primary rounded border border-primary");
                pub3.setClassContent("service-content bg-secondary text-center p-4 rounded");
                pub3.setClassTitre("text-white");
                publicites.add(pub3);

                publiciteRepository.saveAll(publicites);
        }
}
