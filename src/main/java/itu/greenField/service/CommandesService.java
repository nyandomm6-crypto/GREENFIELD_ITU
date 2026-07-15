package itu.greenField.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import itu.greenField.dto.CommandeBackFilterDto;
import itu.greenField.dto.CommandeBackFormDto;
import itu.greenField.dto.DetailCommandeBackDto;
import itu.greenField.dto.DetailCommandeExelDto;
import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;
import itu.greenField.model.ModeReception;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.Produit;
import itu.greenField.model.StatutCommande;
import itu.greenField.model.TypeCommande;
import itu.greenField.model.HistoriqueStatutCommande;
import itu.greenField.model.ProvinceLivraison;
import itu.greenField.model.FraisLivraison;
import itu.greenField.repository.CommandesRepository;
import itu.greenField.repository.DetailsCommandeRepository;
import itu.greenField.model.MvtStock;

import org.springframework.data.domain.Pageable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.Query;

@Service
@RequiredArgsConstructor
public class CommandesService {
    @PersistenceContext
    private EntityManager em;
    private final ProduitService produitService;
    private final CommandesRepository commandesRepository;
    private final ClientService clientService;
    private final PointDeVenteService pointDeVenteService;
    private final DetailsCommandeRepository detailsCommandeRepository;
    private final StatutCommandeService statutCommandeService;
    private final HistoriqueStatutCommandeService historiqueStatutCommandeService;
    private final ProvinceLivraisonService provinceLivraisonService;
    private final FraisLivraisonService fraisLivraisonService;
    private final ImportCommandeExelService importCommandeExelService;
    private final EntityExcelService entityExcelService;
    private final MvtStockService mvtStockService;

    public List<Commandes> getCommandesDispo() {
        return commandesRepository.findDispoCommandes();
    }

    /** Commandes créées par le point de vente donné (par code) — utilisé par l'espace caissier. */
    public List<Commandes> findByPointDeVenteCreateur(String code) {
        if (code == null || code.isBlank()) {
            return new ArrayList<>();
        }
        return commandesRepository.findByPointDeVenteCreateur_CodeOrderByIdDesc(code);
    }

    public Commandes getCommandeById(Integer id) {
        return commandesRepository.findById(id).orElse(null);
    }

    public Page<Commandes> getCommandesPagine(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commandesRepository.findAllPaginated(pageable);
    }

    public void delete(Commandes cmd) throws Exception {
        // throw new Exception();
        commandesRepository.delete(cmd);
    }

    public Commandes findById(Integer id) {
        return commandesRepository.findById(id).orElse(null);
    }

    public void checkIfUpdatable(Commandes cmd) throws Exception {
        StatutCommande currentStatut = cmd.getStatutActuel();
        if (currentStatut.getNom().equals("Livrée")) {
            throw new Exception("Une commande livrée ne peut plus être modifiée!");
        }
        if (currentStatut.getNom().equals("Anulée")) {
            throw new Exception("Une commande anulée ne peut plus être modifiée!");
        }
    }

    @Transactional
    public void livrerRetraitBoutiqueCommande(Integer idCommande) throws Exception {
        Commandes cmd = commandesRepository.findById(idCommande).orElse(null);
        if (cmd == null) {
            throw new RuntimeException("Commande introuvable avec l'ID: " + idCommande);
        }
        StatutCommande statutLivree = statutCommandeService.findByNom("Livrée");
        if (statutLivree == null) {
            throw new RuntimeException("Statut commande \"Livrée\" n'existe pas");
        }
        cmd.setStatutActuel(statutLivree);
        commandesRepository.save(cmd);

        MvtStock mvtStock = mvtStockService.saveMvtStock(cmd);

        HistoriqueStatutCommande hist = new HistoriqueStatutCommande();
        hist.setStatutCommande(statutLivree);
        hist.setCommande(cmd);
        hist.setDatechangement(new java.sql.Timestamp(System.currentTimeMillis()));
        historiqueStatutCommandeService.save(hist);
    }

    @Transactional
    public Commandes saveBackCommande(CommandeBackFormDto commandeFormDto) throws Exception {
        return saveCommande(commandeFormDto, TypeCommande.En_boutique, commandeFormDto.getPointDeVenteId());
    }

    @Transactional
    public Commandes saveFrontCommande(CommandeBackFormDto commandeFormDto) throws Exception {
        return saveCommande(commandeFormDto, TypeCommande.En_ligne, commandeFormDto.getPointDeVenteId());
    }

    @Transactional
    private Commandes saveCommande(CommandeBackFormDto commandeFormDto, TypeCommande typeCommande,
            Integer pointDeVenteId) throws Exception {
        Integer clientId = commandeFormDto.getClientId();
        Client client = null;
        if (clientId != null) {
            client = clientService.getClientById(clientId);
        } else {
            client = new Client();
            client.setNom(commandeFormDto.getClientNom());
            client.setPrenom(commandeFormDto.getClientPrenom());
            client = clientService.saveClient(client);
        }

        ModeReception modeReception = ModeReception.fromString(commandeFormDto.getModeReception());

        Commandes commande = null;
        HistoriqueStatutCommande hist = null;
        if (commandeFormDto.getCommandeId() != null) {
            commande = commandesRepository.findById(commandeFormDto.getCommandeId()).orElse(null);
            if (commande == null) {
                throw new Exception("Commande introuvable avec l'ID: " + commandeFormDto.getCommandeId());
            }
            StatutCommande currentStatut = commande.getStatutActuel();
            if (currentStatut.getNom().equals("Livrée")) {
                throw new Exception("Une commande livrée ne peut plus être modifiée!");
            }
            if (currentStatut.getNom().equals("Anulée")) {
                throw new Exception("Une commande anulée ne peut plus être modifiée!");
            }
            detailsCommandeRepository.deleteAll(commande.getDetailsCommande());
        } else {
            commande = new Commandes();
            commande.setClient(client);
            commande.setDatecommande(commandeFormDto.getSqlTypeOfDate());
            commande.setTypeCommande(typeCommande);

            StatutCommande statusCommande = statutCommandeService.findByNom("Créée");
            if (statusCommande == null)
                throw new Exception("Statut commande \"Créée\" n'existe pas");
            hist = new HistoriqueStatutCommande();
            hist.setStatutCommande(statusCommande);
            hist.setCommande(commande);
            hist.setDatechangement(commandeFormDto.getSqlTypeOfDate());
            commande.setStatutActuel(statusCommande);
        }

        commande.setModeReception(modeReception);
        commande.setHeureReceptionDebut(commandeFormDto.getSqlTypeOfHeureReceptionDebut());
        commande.setHeureReceptionFin(commandeFormDto.getSqlTypeOfHeureReceptionFin());
        commande.setAdresseLivraison(commandeFormDto.getAddress());

        if(commandeFormDto.getCodePointDeVendeCreateur() != null && !commandeFormDto.getCodePointDeVendeCreateur().isEmpty()){
            PointDeVente pdvCreateur = pointDeVenteService.findPointDeVenteByCode(commandeFormDto.getCodePointDeVendeCreateur());
            if(pdvCreateur != null){
                commande.setPointDeVenteCreateur(pdvCreateur);
            }
        }
            

        /* Static pour le moment */
        if (commandeFormDto.getAddress() == null || modeReception == ModeReception.Retrait_Boutique) {
            Integer idPdv = pointDeVenteId == null ? 1 : pointDeVenteId;
            PointDeVente pdv = pointDeVenteService.findPointDeVenteById(idPdv);
            commande.setPointDeVenteRetrait(pdv);
            commande.setAdresseLivraison(null);
            commande.setProvinceLivraison(null);
        } else {
            ProvinceLivraison provinceLivraison = provinceLivraisonService
                    .getProvinceById(commandeFormDto.getProvinceId());
            commande.setProvinceLivraison(provinceLivraison);
            commande.setPointDeVenteRetrait(null);
        }

        int qteTotal = 0;
        BigDecimal prixTotal = BigDecimal.ZERO;
        BigDecimal poidsTotal = BigDecimal.ZERO;

        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        commande.setPoidsTotal(poidsTotal);

        commande = commandesRepository.save(commande);
        int nbLines = commandeFormDto.getDetailsCommande().size();
        for (int i = 0; i < nbLines; i++) {
            DetailCommandeBackDto detailDto = commandeFormDto.getDetailsCommande().get(i);
            String matricule = detailDto.getProduitMatricule();
            Integer quantite = detailDto.getQuantite();
            DetailsCommande detail = new DetailsCommande();
            Produit produit = produitService.findProduitByMatricule(matricule);
            detail.setCommande(commande);
            detail.setProduit(produit);
            detail.setQuantite(quantite);
            detail.setPuAuMomentAchat(produit.getPu());
            detailsCommandeRepository.save(detail);

            qteTotal += quantite;
            prixTotal = prixTotal.add((BigDecimal.valueOf(quantite)).multiply(produit.getPu()));
            poidsTotal = poidsTotal.add((BigDecimal.valueOf(quantite)).multiply(produit.getPoids()));
        }

        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        commande.setPoidsTotal(poidsTotal);

        commande.setFraisLivraison(BigDecimal.ZERO);
        if (modeReception == ModeReception.Livraison_Domicile) {
            FraisLivraison fraisLivraison = fraisLivraisonService
                    .calculateFraisLivraison(commande.getProvinceLivraison().getId(), poidsTotal.doubleValue());
            commande.setFraisLivraison(fraisLivraison.getMontant());
        }

        commande = commandesRepository.save(commande);

        if (hist != null)
            historiqueStatutCommandeService.save(hist);

        return commande;
    }

    public Page<Commandes> findWithDynamicFilters(CommandeBackFilterDto filter) {
        int page = filter.getPageNumber();
        int size = filter.getLineNumber();
        Pageable pageable = PageRequest.of(page - 1, size);
        StringBuilder sb = new StringBuilder("SELECT * FROM commandes c WHERE 1 = 1 ");
        StringBuilder sbCount = new StringBuilder("SELECT count(*) FROM commandes c WHERE 1 = 1 ");
        Map<String, Object> params = new HashMap<>();

        // 1. Filtre Statut (statutcommande)
        if (filter.getStatutCommande() != null && !filter.getStatutCommande().isEmpty()) {
            sb.append("AND c.statutactuel = :statut ");
            sbCount.append("AND c.statutactuel = :statut ");
            params.put("statut", Integer.valueOf(filter.getStatutCommande()));
        }

        // 2. Filtre Mode Réception (mode_reception)
        if (filter.getModeReception() != null && !filter.getModeReception().isEmpty()) {
            sb.append("AND c.mode_reception = :mode ");
            sbCount.append("AND c.mode_reception = :mode ");
            params.put("mode", filter.getModeReception());
        }

        if (filter.getPointDeVente() != null && !filter.getPointDeVente().isEmpty()) {
            if(filter.getPointDeVente().equals("null")) {
                sb.append("AND c.idptvente_createur IS NULL ");
                sbCount.append("AND c.idptvente_createur IS NULL ");
            } else {
                sb.append("AND (c.idptvente_createur = :pointDeVente) ");
                sbCount.append("AND (c.idptvente_createur = :pointDeVente) ");
                params.put("pointDeVente", filter.getPointDeVente());
            }
        }

        // 3. Filtre Multi-Clients (idclient)
        if (filter.getClientId() != null && !filter.getClientId().isEmpty()) {
            sb.append("AND c.idclient IN (:clients) ");
            sbCount.append("AND c.idclient IN (:clients) ");
            params.put("clients", filter.getClientId());
        }

        // 3.5. Filtre Type Commande (type_commande)
        if (filter.getTypeCommande() != null && !filter.getTypeCommande().isEmpty()) {
            sb.append("AND c.type_commande = :type ");
            sbCount.append("AND c.type_commande = :type ");
            params.put("type", filter.getTypeCommande());
        }

        // 4. Boucle pour les DATES dynamiques (datecommande, heure_reception_debut,
        // heure_reception_fin)
        if (filter.getTypeFiltreDate() != null) {
            for (int i = 0; i < filter.getTypeFiltreDate().size(); i++) {
                String colonne = filter.getTypeFiltreDate().get(i);
                String opSign = filter.getOperateurDate().get(i); // Utilise directement "=", "<", ">=", etc.
                String paramName = "dateVal_" + i;

                sb.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                sbCount.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                params.put(paramName, filter.getDateValue().get(i));
            }
        }

        // 5. Boucle pour les NOMBRES dynamiques (total_produits, total_general,
        // frais_livraison)
        if (filter.getTypeFiltreNombre() != null) {
            for (int i = 0; i < filter.getTypeFiltreNombre().size(); i++) {
                String colonne = filter.getTypeFiltreNombre().get(i);
                String opSign = filter.getOperateurNombre().get(i); // Utilise directement "=", "<", etc.
                String paramName = "numVal_" + i;

                sb.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                sbCount.append("AND c.").append(colonne).append(" ").append(opSign).append(" :").append(paramName)
                        .append(" ");
                params.put(paramName, filter.getNombreValue().get(i));
            }
        }

        Query query = em.createNativeQuery(sb.toString(), Commandes.class);
        Query countQuery = em.createNativeQuery(sbCount.toString());

        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Calcul du total d'éléments correspondants aux filtres
        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Commandes> resultList = query.getResultList();

        return new PageImpl<Commandes>(resultList, pageable, total);
    }

    /** En-têtes du fichier Excel d'export des commandes. */
    public static final String[] EXPORT_HEADERS = {
            "ID", "Date", "Client", "Mode de réception", "Type", "Statut",
            "Total produits", "Frais livraison", "Total général", "Point de vente / Adresse"
    };

    /**
     * Exporte au format Excel les commandes correspondant aux filtres courants
     * (mêmes filtres que la liste, mais sans pagination).
     */
    public byte[] exportCommandesExcel(CommandeBackFilterDto filter) throws Exception {
        if (filter == null) {
            filter = new CommandeBackFilterDto();
        }
        // Récupérer toutes les lignes correspondant aux filtres (pas de pagination).
        filter.setPageNumber(1);
        filter.setLineNumber(Integer.MAX_VALUE);
        List<Commandes> commandes = findWithDynamicFilters(filter).getContent();

        List<Object[]> rows = new ArrayList<>();
        for (Commandes c : commandes) {
            String client = c.getClient() != null
                    ? (c.getClient().getNom() + " " + c.getClient().getPrenom()).trim()
                    : "";
            String lieu;
            if (c.getPointDeVenteRetrait() != null) {
                lieu = c.getPointDeVenteRetrait().getNom();
            } else if (c.getAdresseLivraison() != null) {
                lieu = c.getAdresseLivraison();
            } else {
                lieu = "";
            }
            rows.add(new Object[] {
                    c.getId(),
                    c.getDatecommande() != null ? c.getDatecommande().toString() : "",
                    client,
                    c.getModeReception() != null ? c.getModeReception().name() : "",
                    c.getTypeCommande() != null ? c.getTypeCommande().name() : "",
                    c.getStatutActuel() != null ? c.getStatutActuel().getNom() : "",
                    c.getTotalProduits(),
                    c.getFraisLivraison(),
                    c.getTotalGeneral(),
                    lieu
            });
        }
        return entityExcelService.export("commandes", EXPORT_HEADERS, rows);
    }

    public byte[] generateTemplateExcelFile(List<Produit> produits) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            generateCommandeSheet(new ArrayList<>(), produits.size(), workbook);
            generateProduitSheet(produits, workbook);
            generateProvinceSheet(provinceLivraisonService.getAllProvinces(), workbook);
            generatePointDeVenteSheet(pointDeVenteService.getAllPointDeVente(), workbook);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    public void generateCommandeSheet(List<Commandes> commandes, int nbProduits, XSSFWorkbook workbook)
            throws Exception {
        try {
            XSSFSheet sheet = workbook.createSheet("commandes_sheet");
            sheet.setDisplayGridlines(true);

            String[] columnHeaders = importCommandeExelService.getCommandeExelHeader();

            String[] columnHeadersDetails = importCommandeExelService.getCommandeDetailExelHeader();

            int rowIndex = 0;
            int columnProductNameIndex = columnHeaders.length + 1; // Colonne J (index 10)

            // 1. On crée la ligne 0 pour le grand titre
            Row headerRow = sheet.createRow(rowIndex);

            Cell header1Cell = headerRow.createCell(0);
            header1Cell.setCellValue("Informations des commandes");

            Cell header2Cell = headerRow.createCell(columnProductNameIndex);
            header2Cell.setCellValue("Détails commandes");

            CellStyle headerstyle = workbook.createCellStyle();
            headerstyle.setAlignment(HorizontalAlignment.CENTER);
            headerstyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Un petit style gras pour que le titre ressorte bien
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerstyle.setFont(headerFont);
            header1Cell.setCellStyle(headerstyle);
            header2Cell.setCellStyle(headerstyle);

            // 2. CORRECTION : On fusionne la ligne 0 (de la colonne A à E -> index 0 à 4)
            CellRangeAddress regionmere = new CellRangeAddress(
                    rowIndex, // vaut 0
                    rowIndex, // vaut 0
                    0,
                    columnHeaders.length - 1);
            sheet.addMergedRegion(regionmere);

            CellRangeAddress regionfille = new CellRangeAddress(
                    rowIndex, // vaut 0
                    rowIndex, // vaut 0
                    columnProductNameIndex,
                    columnHeadersDetails.length + columnHeaders.length);
            sheet.addMergedRegion(regionfille);

            // 3. On passe maintenant à la ligne suivante pour les en-têtes de colonnes
            rowIndex++; // rowIndex passe à 1

            Row columnHeaderRow = sheet.createRow(rowIndex); // Crée la ligne 1, puis passe à 2
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            }

            int startIndex = columnHeaders.length + 1; // Commence après les colonnes de commandes
            for (int i = 0; i < columnHeadersDetails.length; i++) {
                Cell cell = columnHeaderRow.createCell(startIndex + i);
                cell.setCellValue(columnHeadersDetails[i]);
            }

            /* Liste deroulante des produit */
            CellRangeAddressList addressProduitList = new CellRangeAddressList(2, 100000, columnProductNameIndex + 1,
                    columnProductNameIndex + 1);

            // 2. Créer le Helper de validation pour le format XLSX
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);

            int startRow = 3; // Ligne de départ pour la validation (après les en-têtes)
            int endRow = startRow + nbProduits - 1; // Ligne de fin pour la validation
            String FieldProduitformula = "produits_sheet!$C$" + startRow + ":$C$" + endRow; // Plage de cellules
                                                                                            // contenant les
            // valeurs valides
            DataValidationConstraint constraint = validationHelper
                    .createFormulaListConstraint(FieldProduitformula);

            DataValidation validationProduit = validationHelper.createValidation(constraint, addressProduitList);
            validationProduit.setShowErrorBox(true);
            validationProduit.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationProduit.createErrorBox("Erreur de validation",
                    "Veuillez sélectionner un produit valide dans la liste déroulante.");

            sheet.addValidationData(validationProduit);

            /* Liste deroulante points de ventes */
            int culumnPointDeVenteIndex = importCommandeExelService.getIndexOfPointDeVenteColumn();
            CellRangeAddressList addressPointDeVenteList = new CellRangeAddressList(2, 100000, culumnPointDeVenteIndex,
                    culumnPointDeVenteIndex);

            // 2. Créer le Helper de validation pour le format XLSX
            XSSFDataValidationHelper validationHelperPointDeVente = new XSSFDataValidationHelper((XSSFSheet) sheet);
            int startRowPointDeVente = 3; // Ligne de départ pour la validation (après les en-têtes)
            int endRowPointDeVente = startRowPointDeVente + pointDeVenteService.getAllPointDeVente().size() - 1; // Ligne
                                                                                                                 // de
                                                                                                                 // fin
                                                                                                                 // pour
                                                                                                                 // la
                                                                                                                 // validation
            String FieldPointDeVenteformula = "point_de_vente_sheet!$B$" + startRowPointDeVente + ":$B$"
                    + endRowPointDeVente; // Plage de cellules contenant les valeurs valides
            DataValidationConstraint constraintPointDeVente = validationHelperPointDeVente
                    .createFormulaListConstraint(FieldPointDeVenteformula);

            DataValidation validationPointDeVente = validationHelperPointDeVente
                    .createValidation(constraintPointDeVente, addressPointDeVenteList);
            validationPointDeVente.setShowErrorBox(true);
            validationPointDeVente.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationPointDeVente.createErrorBox("Erreur de validation",
                    "Veuillez sélectionner un point de vente valide dans la liste déroulante.");
            sheet.addValidationData(validationPointDeVente);

            /* Liste deroulante des modes de réception */
            String[] valideMode = { "Retrait", "Livraison" };
            int indexModeReceptionCol = importCommandeExelService.getIndexOfModeReceptionColumn();

            CellRangeAddressList addressModeReceptionList = new CellRangeAddressList(2, 100000, indexModeReceptionCol,
                    indexModeReceptionCol);
            XSSFDataValidationHelper validationHelperModeReception = new XSSFDataValidationHelper((XSSFSheet) sheet);
            DataValidationConstraint constraintModeReception = validationHelperModeReception
                    .createExplicitListConstraint(valideMode);
            DataValidation validationModeReception = validationHelperModeReception
                    .createValidation(constraintModeReception, addressModeReceptionList);
            validationModeReception.setShowErrorBox(true);
            validationModeReception.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationModeReception.createErrorBox("Erreur de validation",
                    "Veuillez sélectionner un mode de réception valide dans la liste déroulante.");
            sheet.addValidationData(validationModeReception);

            /* Liste deroulante des provinces */
            int indexProvinceColumn = 8; // Colonne I (index 8) pour la province
            int provinceStartRow = startRow; // Ligne de départ pour la validation (après les en-têtes)
            int provinceEndRow = provinceStartRow + provinceLivraisonService.getAllProvinces().size() - 1;
            String FieldProvinceformula = "provinces_sheet!$B$" + provinceStartRow + ":$B$" + provinceEndRow;

            XSSFDataValidationHelper validationHelperProvince = new XSSFDataValidationHelper((XSSFSheet) sheet);

            CellRangeAddressList addressProvinceList = new CellRangeAddressList(2, 100000, indexProvinceColumn,
                    indexProvinceColumn);

            DataValidationConstraint constraintProvince = validationHelperProvince
                    .createFormulaListConstraint(FieldProvinceformula);

            DataValidation validationProvince = validationHelperProvince.createValidation(constraintProvince,
                    addressProvinceList);
            validationProvince.setShowErrorBox(true); // Supprime la flèche de la liste déroulante
            validationProvince.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationProvince.createErrorBox("Erreur de validation",
                    "Veuillez sélectionner une province valide dans la liste déroulante.");

            sheet.addValidationData(validationProvince);

            /* validation pour quantie > 0 */
            int indexQuantiteColumn = importCommandeExelService.getIndexOfQuantiteColumn();
            CellRangeAddressList addressQuantiteList = new CellRangeAddressList(2, 100000, indexQuantiteColumn,
                    indexQuantiteColumn);
            XSSFDataValidationHelper validationHelperQuantite = new XSSFDataValidationHelper((XSSFSheet) sheet);
            DataValidationConstraint constraintQuantite = validationHelperQuantite.createNumericConstraint(
                    DataValidationConstraint.ValidationType.INTEGER,
                    DataValidationConstraint.OperatorType.GREATER_THAN,
                    "0", null);
            DataValidation validationQuantite = validationHelperQuantite.createValidation(constraintQuantite,
                    addressQuantiteList);
            validationQuantite.setShowErrorBox(true);
            validationQuantite.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationQuantite.createErrorBox("Erreur de validation",
                    "La quantité doit être un entier supérieur à 0.");
            sheet.addValidationData(validationQuantite);

            int rowCount = 1;
            for (int i = 2; i < 200; i++) {
                XSSFRow row = (XSSFRow) sheet.getRow(i);
                if (row == null) {
                    row = (XSSFRow) sheet.createRow(i);
                }

                // Num de commande
                XSSFCell cellNumCommande = row.createCell(0);
                cellNumCommande.setCellValue(rowCount++);

                // Le prix unitaire est dans la colonne M (index 12)
                XSSFCell cellPrix = row.createCell(12);

                int excelLine = i + 1;
                String fieldPUFormula = "IF(K" + excelLine + "=\"\", \"\", VLOOKUP(K" + excelLine
                        + ", produits_sheet!$C$" + startRow + ":$E$" + endRow + ", 2, FALSE))";

                cellPrix.setCellFormula(fieldPUFormula);
            }

            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
            for (int i = 0; i < columnHeadersDetails.length; i++) {
                sheet.autoSizeColumn(startIndex + i);
            }

        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    public void generateProduitSheet(List<Produit> produits, XSSFWorkbook workbook) throws Exception {
        try {
            XSSFSheet sheet = workbook.createSheet("produits_sheet");
            sheet.setDisplayGridlines(true); // Pour s'assurer que la grille reste visible

            int rowIndex = 0;

            // 1. On crée la ligne 0 pour le grand titre
            Row headerRow = sheet.createRow(rowIndex);

            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Informations des produits");

            CellStyle headerstyle = workbook.createCellStyle();
            headerstyle.setAlignment(HorizontalAlignment.CENTER);
            headerstyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Un petit style gras pour que le titre ressorte bien
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerstyle.setFont(headerFont);
            headerCell.setCellStyle(headerstyle);

            // 2. CORRECTION : On fusionne la ligne 0 (de la colonne A à E -> index 0 à 4)
            CellRangeAddress region = new CellRangeAddress(
                    rowIndex, // vaut 0
                    rowIndex, // vaut 0
                    0,
                    4); // 4 car il y a 5 colonnes au total (0, 1, 2, 3, 4)
            sheet.addMergedRegion(region);

            // 3. On passe maintenant à la ligne suivante pour les en-têtes de colonnes
            rowIndex++; // rowIndex passe à 1

            Row columnHeaderRow = sheet.createRow(rowIndex++); // Crée la ligne 1, puis passe à 2
            String[] columnHeaders = { "Num_produit", "Matricule", "Nom", "Prix_Unitaire", "Poids" };
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            }

            // 4. Écriture des produits à partir de la ligne 2
            for (Produit produit : produits) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(produit.getId());
                row.createCell(1).setCellValue(produit.getMatricule());
                row.createCell(2).setCellValue(produit.getNom());
                row.createCell(3).setCellValue(produit.getPu().doubleValue());
                row.createCell(4).setCellValue(produit.getPoids().doubleValue());
            }

            // Ajustement automatique
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    public void generateProvinceSheet(List<ProvinceLivraison> provinces, XSSFWorkbook workbook) throws Exception {
        try {
            XSSFSheet sheet = workbook.createSheet("provinces_sheet");
            sheet.setDisplayGridlines(true); // Pour s'assurer que la grille reste visible

            int rowIndex = 0;

            // 1. On crée la ligne 0 pour le grand titre
            Row headerRow = sheet.createRow(rowIndex);

            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Informations des provinces");

            CellStyle headerstyle = workbook.createCellStyle();
            headerstyle.setAlignment(HorizontalAlignment.CENTER);
            headerstyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Un petit style gras pour que le titre ressorte bien
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerstyle.setFont(headerFont);
            headerCell.setCellStyle(headerstyle);

            // 2. CORRECTION : On fusionne la ligne 0 (de la colonne A à E -> index 0 à 4)
            CellRangeAddress region = new CellRangeAddress(
                    rowIndex, // vaut 0
                    rowIndex, // vaut 0
                    0,
                    1); // 1 car il y a 2 colonnes au total (0, 1)
            sheet.addMergedRegion(region);

            // 3. On passe maintenant à la ligne suivante pour les en-têtes de colonnes
            rowIndex++; // rowIndex passe à 1

            Row columnHeaderRow = sheet.createRow(rowIndex++); // Crée la ligne 1, puis passe à 2
            String[] columnHeaders = { "Num_province", "Nom" };
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            }

            // 4. Écriture des provinces à partir de la ligne 2
            for (ProvinceLivraison province : provinces) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(province.getId());
                row.createCell(1).setCellValue(province.getNom());
            }

            // Ajustement automatique
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    public void generatePointDeVenteSheet(List<PointDeVente> pointDeVentes, XSSFWorkbook workbook) throws Exception {
        try {
            XSSFSheet sheet = workbook.createSheet("point_de_vente_sheet");
            sheet.setDisplayGridlines(true); // Pour s'assurer que la grille reste visible

            int rowIndex = 0;

            // 1. On crée la ligne 0 pour le grand titre
            Row headerRow = sheet.createRow(rowIndex);

            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Informations des points de vente");

            CellStyle headerstyle = workbook.createCellStyle();
            headerstyle.setAlignment(HorizontalAlignment.CENTER);
            headerstyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Un petit style gras pour que le titre ressorte bien
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerstyle.setFont(headerFont);
            headerCell.setCellStyle(headerstyle);

            // 2. CORRECTION : On fusionne la ligne 0 (de la colonne A à E -> index 0 à 4)
            CellRangeAddress region = new CellRangeAddress(
                    rowIndex, // vaut 0
                    rowIndex, // vaut 0
                    0,
                    1); // 1 car il y a 2 colonnes au total (0, 1)
            sheet.addMergedRegion(region);

            // 3. On passe maintenant à la ligne suivante pour les en-têtes de colonnes
            rowIndex++; // rowIndex passe à 1

            Row columnHeaderRow = sheet.createRow(rowIndex++); // Crée la ligne 1, puis passe à 2
            String[] columnHeaders = { "Code", "Nom" };
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            }

            // 4. Écriture des points de vente à partir de la ligne 2
            for (PointDeVente pointDeVente : pointDeVentes) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(pointDeVente.getId());
                row.createCell(1).setCellValue(pointDeVente.getNom());
            }

            // Ajustement automatique
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    public void saveDataFromExcelUpload(InputStream inputStream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<Integer, Commandes> idToCommandeMap = new LinkedHashMap<>();

        Map<Integer, List<DetailCommandeExelDto>> commandeDetailsMap = new HashMap<>();

        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex <= 1) {
                rowIndex++;
                continue;
            }

            Cell firstCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String firstCellValue = firstCell.toString().trim();

            if (!firstCellValue.isEmpty()) {
                Commandes nouvelleCommande = new Commandes();
                if (importCommandeExelService.isCommandeValid(row, nouvelleCommande)) {
                    if (!idToCommandeMap.containsKey(nouvelleCommande.getId())) {
                        idToCommandeMap.put(nouvelleCommande.getId(), nouvelleCommande);
                        commandeDetailsMap.put(nouvelleCommande.getId(), new ArrayList<>());
                        System.out.println("[✅ Commande] Détectée et valide ID: " + nouvelleCommande.getId()
                                + " à la ligne " + (rowIndex + 1));
                    }
                } else {
                    System.out.println("[⚠️ Erreur] Commande invalide détectée à la ligne " + (rowIndex + 1));
                }
            }

            int scale = importCommandeExelService.getCommandeExelHeader().length + 1; // Index de la colonne M
            Cell cellNumCmdDetail = row.getCell(scale, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String numCmdDetailStr = cellNumCmdDetail.toString().trim();

            if (!numCmdDetailStr.isEmpty()) {
                DetailCommandeExelDto detailCommande = new DetailCommandeExelDto();

                int targetCommandId;
                try {
                    if (cellNumCmdDetail.getCellType() == CellType.NUMERIC) {
                        targetCommandId = (int) cellNumCmdDetail.getNumericCellValue();
                    } else {
                        if (numCmdDetailStr.contains(".")) {
                            numCmdDetailStr = numCmdDetailStr.split("\\.")[0];
                        }
                        targetCommandId = Integer.valueOf(numCmdDetailStr);
                    }
                } catch (Exception e) { // premiere col illisible
                    rowIndex++;
                    continue;
                }

                if (importCommandeExelService.isCommandeDetailValid(row, targetCommandId, detailCommande)) {
                    // On ajoute ce produit à la liste de la commande cible (même si elle a été
                    // déclarée plus haut)
                    if (commandeDetailsMap.containsKey(targetCommandId)) {
                        commandeDetailsMap.get(targetCommandId).add(detailCommande);
                        System.out.println("[📦 Produit] " + detailCommande.getProduit().getNom()
                                + " associé à la Commande ID: " + targetCommandId);
                    } else {
                        System.out.println("[⚠️ Orphelin] Le produit à la ligne " + (rowIndex + 1)
                                + " référence une Commande ID " + targetCommandId + " introuvable dans le fichier.");
                    }
                }
            }

            if (firstCellValue.isEmpty() && numCmdDetailStr.isEmpty()) {
                System.out.println("[Import] Fin du fichier atteinte à la ligne " + (rowIndex + 1));
                break;
            }

            rowIndex++;
        }

        System.out.println("[Database] Lancement des enregistrements...");
        for (Map.Entry<Integer, Commandes> entry : idToCommandeMap.entrySet()) {
            Commandes cmd = entry.getValue();
            List<DetailCommandeExelDto> details = commandeDetailsMap.get(cmd.getId());

            System.out.println(
                    " -> Enregistrement Commande ID: " + cmd.getId() + " avec " + details.size() + " produit(s).");
            saveCommandesFromExcel(cmd, details);
        }

        workbook.close();
    }

    @Transactional
    public void saveCommandesFromExcel(Commandes commande, List<DetailCommandeExelDto> details) throws Exception {
        // 1. On force l'ID à null pour une nouvelle insertion
        commande.setId(null);

        int qteTotal = 0;
        BigDecimal prixTotal = BigDecimal.ZERO;
        BigDecimal poidsTotal = BigDecimal.ZERO;

        // 2. PRE-CALCUL : On calcule tout AVANT d'enregistrer quoi que ce soit
        List<DetailsCommande> entitesDetails = new ArrayList<>();

        for (DetailCommandeExelDto detail : details) {
            DetailsCommande dc = new DetailsCommande();
            dc.setProduit(detail.getProduit());
            dc.setQuantite(detail.getQuantite());
            dc.setPuAuMomentAchat(detail.getProduit().getPu());

            entitesDetails.add(dc);

            // Cumul des totaux
            qteTotal += detail.getQuantite();
            prixTotal = prixTotal.add(BigDecimal.valueOf(detail.getQuantite()).multiply(detail.getProduit().getPu()));
            poidsTotal = poidsTotal
                    .add(BigDecimal.valueOf(detail.getQuantite()).multiply(detail.getProduit().getPoids()));
        }

        // 3. On affecte les totaux calculés directement à l'objet commande
        commande.setTotalProduits(qteTotal);
        commande.setTotalGeneral(prixTotal);
        commande.setPoidsTotal(poidsTotal);

        // 4. Calcul des frais de livraison
        commande.setFraisLivraison(BigDecimal.ZERO);
        if (commande.getModeReception() == ModeReception.Livraison_Domicile) {
            FraisLivraison fraisLivraison = fraisLivraisonService
                    .calculateFraisLivraison(commande.getProvinceLivraison().getId(), poidsTotal.doubleValue());
            commande.setFraisLivraison(fraisLivraison.getMontant());
        }

        // 5. Un SEUL save pour la commande (elle part en BDD avec les bons totaux dès
        // le départ)
        commande = commandesRepository.save(commande);

        // 6. On lie la commande persistée aux détails et on les enregistre
        for (DetailsCommande dc : entitesDetails) {
            dc.setCommande(commande); // Maintenant l'ID de la commande est connu
            detailsCommandeRepository.save(dc);
        }

        // 7. Gestion du statut historique
        StatutCommande statusCommande = statutCommandeService.findByNom("Créée");
        if (statusCommande == null) {
            throw new Exception("Statut commande \"Créée\" n'existe pas");
        }

        HistoriqueStatutCommande hist = new HistoriqueStatutCommande();
        hist.setStatutCommande(statusCommande);
        hist.setCommande(commande);
        hist.setDatechangement(commande.getDatecommande());
        commande.setStatutActuel(statusCommande);

        historiqueStatutCommandeService.save(hist);
    }

}
