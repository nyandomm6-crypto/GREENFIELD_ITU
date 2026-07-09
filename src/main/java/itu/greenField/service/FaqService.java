package itu.greenField.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itu.greenField.model.Faq;
import itu.greenField.repository.FaqRepository;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> findAll() {
        return faqRepository.findByActiveTrueOrderByOrdreAsc();
    }

    @Transactional
    public void seedDefaultFaqsIfEmpty() {
        if (faqRepository.count() > 0) {
            return;
        }

        List<Faq> faqs = new ArrayList<>();
        faqs.add(createFaq("Comment se passe la livraison ?",
                "Nous livrons dans toute l'île en 24 à 48 heures selon votre région.", 1));
        faqs.add(createFaq("Quels moyens de paiement acceptez-vous ?",
                "Vous pouvez payer par MVola, Orange Money, Airtel Money ou par carte bancaire.", 2));
        faqs.add(createFaq("Puis-je retourner un produit ?",
                "Oui, vous avez 7 jours pour retourner un produit non ouvert et conforme à la politique de retour.",
                3));
        faqs.add(createFaq("D'où viennent vos produits ?",
                "Nos produits proviennent de producteurs locaux engagés dans une agriculture durable.", 4));

        faqRepository.saveAll(faqs);
    }

    private Faq createFaq(String question, String reponse, int ordre) {
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setReponse(reponse);
        faq.setActive(true);
        faq.setOrdre(ordre);
        return faq;
    }
}
