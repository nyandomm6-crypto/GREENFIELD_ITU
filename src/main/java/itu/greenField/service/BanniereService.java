package itu.greenField.service;

import itu.greenField.model.Banniere;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BanniereService {

    public List<Banniere> findAll() {
        List<Banniere> bannieres = new ArrayList<>();

        Banniere banniere = new Banniere();
        banniere.setId(1L);
        banniere.setTitre("Fresh Exotic Fruits");
        banniere.setSousTitre("in Our Store");
        banniere.setDescription(
                "The generated Lorem Ipsum is therefore always free from repetition injected humour, or non-characteristic words etc.");
        banniere.setImagePath("img/baner-1.png");
        banniere.setLien("#");
        banniere.setBtnTexte("BUY");
        banniere.setPromoNombre("1");
        banniere.setPromoPrix("50$");
        banniere.setPromoUnite("kg");
        bannieres.add(banniere);

        return bannieres;
    }
}