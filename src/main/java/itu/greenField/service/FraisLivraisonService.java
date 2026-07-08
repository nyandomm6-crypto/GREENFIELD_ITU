package itu.greenfield.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import itu.greenField.dto.FraisLivraisonFilterDto;
import itu.greenField.dto.FraisLivraisonFormDto;
import itu.greenField.model.FraisLivraison;
import itu.greenField.model.ProvinceLivraison;
import itu.greenField.repository.FraisLivraisonRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FraisLivraisonService {
    @PersistenceContext
    private EntityManager em;
    private final FraisLivraisonRepository fraisLivraisonRepository;
    private final ProvinceLivraisonService provinceLivraisonService;

    public FraisLivraisonService(FraisLivraisonRepository fraisLivraisonRepository,
            ProvinceLivraisonService provinceLivraisonService
    ) {
        this.fraisLivraisonRepository = fraisLivraisonRepository;
        this.provinceLivraisonService = provinceLivraisonService;
    }

    public FraisLivraison calculateFraisLivraison(Integer provinceId, Double poids) {
        FraisLivraison fraisLivraison = fraisLivraisonRepository
                .findFirstByProvinceLivraisonIdAndPoidsReferenceGreaterThanOrderByPoidsReferenceAsc(provinceId, poids)
                .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé pour le poids : " + poids));
        return fraisLivraison;
    }

    public List<FraisLivraison> getAllFraisLivraison() {
        return fraisLivraisonRepository.findAll();
    }

    public FraisLivraison getFraisById(Integer id) {
        return fraisLivraisonRepository.findById(id)
                .orElse(null);
    }

    public FraisLivraison saveOrUpdateFrais(FraisLivraisonFormDto form) throws Exception {
        FraisLivraison frais;
        if (form.getId() != null) {
            frais = fraisLivraisonRepository.findById(form.getId())
                    .orElseThrow(() -> new Exception("Frais de livraison introuvable avec l'ID : " + form.getId()));
        } else {
            frais = new FraisLivraison();
        }

        if (form.getIdProvince() != null) {
            ProvinceLivraison province = provinceLivraisonService.getProvinceById(form.getIdProvince());
            frais.setProvinceLivraison(province);
        } else {
            frais.setProvinceLivraison(null);
        }

        frais.setPoidsReference(form.getPoidsreference());
        frais.setMontant(form.getMontant());

        return fraisLivraisonRepository.save(frais);
    }

    public Page<FraisLivraison> findWithDynamicFilters(FraisLivraisonFilterDto filter) {
        int page = filter.getPageNumber();
        int size = filter.getLineNumber();
        Pageable pageable = PageRequest.of(page - 1, size);

        // Initialisation des requêtes SQL natives
        StringBuilder sb = new StringBuilder("SELECT * FROM fraisLivraison f WHERE 1 = 1 ");
        StringBuilder sbCount = new StringBuilder("SELECT count(*) FROM fraisLivraison f WHERE 1 = 1 ");
        Map<String, Object> params = new HashMap<>();

        // 1. Filtre Multi-Provinces (idprovince)
        if (filter.getIdProvince() != null && !filter.getIdProvince().isEmpty()) {
            sb.append("AND f.idprovince IN (:provinces) ");
            sbCount.append("AND f.idprovince IN (:provinces) ");
            params.put("provinces", filter.getIdProvince());
        }

        // 2. Boucle pour les filtres NOMBRES dynamiques (poidsreference, montant)
        if (filter.getTypeFiltreNombre() != null) {
            for (int i = 0; i < filter.getTypeFiltreNombre().size(); i++) {
                String colonne = filter.getTypeFiltreNombre().get(i);
                String opSign = filter.getOperateurNombre().get(i); // Ex: "=", "<=", ">="
                String paramName = "numVal_" + i;

                if ("poidsreference".equalsIgnoreCase(colonne) || "montant".equalsIgnoreCase(colonne)) {
                    sb.append("AND f.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                            .append(" ");
                    sbCount.append("AND f.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                            .append(" ");
                    params.put(paramName, filter.getNombreValue().get(i));
                }
            }
        }

        // Création des objets Query de l'EntityManager
        Query query = em.createNativeQuery(sb.toString(), FraisLivraison.class);
        Query countQuery = em.createNativeQuery(sbCount.toString());

        // Assignation des paramètres liés aux filtres
        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        // Gestion de la pagination (Offset et Limit)
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Calcul du total global pour la pagination
        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<FraisLivraison> resultList = query.getResultList();

        // Retourne le format de Page standard de Spring Data
        return new PageImpl<FraisLivraison>(resultList, pageable, total);
    }
}
