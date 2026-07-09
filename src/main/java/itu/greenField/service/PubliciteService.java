package itu.greenField.service;

import itu.greenField.model.Publicite;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PubliciteService {

        public List<Publicite> findAll() {
                List<Publicite> publicites = new ArrayList<>();

                // Publicité 1
                Publicite pub1 = new Publicite();
                pub1.setImagePath("img/featur-1.jpg");
                pub1.setTitre("Fresh Apples");
                pub1.setSousTitre("20% OFF");
                pub1.setLien("#");
                pub1.setClassDiv("service-item bg-secondary rounded border border-secondary");
                pub1.setClassContent("service-content bg-primary text-center p-4 rounded");
                pub1.setClassTitre("text-white");
                publicites.add(pub1);

                // Publicité 2
                Publicite pub2 = new Publicite();
                pub2.setImagePath("img/featur-2.jpg");
                pub2.setTitre("Tasty Fruits");
                pub2.setSousTitre("Free delivery");
                pub2.setLien("#");
                pub2.setClassDiv("service-item bg-dark rounded border border-dark");
                pub2.setClassContent("service-content bg-light text-center p-4 rounded");
                pub2.setClassTitre("text-primary");
                publicites.add(pub2);

                // Publicité 3
                Publicite pub3 = new Publicite();
                pub3.setImagePath("img/featur-3.jpg");
                pub3.setTitre("Exotic Vegetable");
                pub3.setSousTitre("Discount 30$");
                pub3.setLien("#");
                pub3.setClassDiv("service-item bg-primary rounded border border-primary");
                pub3.setClassContent("service-content bg-secondary text-center p-4 rounded");
                pub3.setClassTitre("text-white");
                publicites.add(pub3);

                return publicites;
        }

        // Méthode pour récupérer une publicité par son ID
        public Publicite findById(Long id) {
                // Implémentez la logique pour récupérer une publicité depuis la base de données
                return null;
        }

        // Méthode pour sauvegarder une publicité
        public Publicite save(Publicite publicite) {
                // Implémentez la logique pour sauvegarder une publicité
                return publicite;
        }

        // Méthode pour supprimer une publicité
        public void deleteById(Long id) {
                // Implémentez la logique pour supprimer une publicité
        }
}