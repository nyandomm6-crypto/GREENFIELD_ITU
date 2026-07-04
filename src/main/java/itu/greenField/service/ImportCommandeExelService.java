package itu.greenField.service;

import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Service;

import itu.greenField.model.Client;
import itu.greenField.model.Commandes;
import itu.greenField.model.ModeReception;
import itu.greenField.model.PointDeVente;
import itu.greenField.model.Produit;
import itu.greenField.model.ProvinceLivraison;
import itu.greenField.dto.DetailCommandeExelDto;

@Service
public class ImportCommandeExelService {
    private PointDeVenteService pointDeVenteService;
    private ClientService clientService;
    private ProvinceLivraisonService provinceLivraisonService;
    private ProduitService produitService;

    private String[] commandeExelHeader = { "Num_commande", "Nom_Client", "Prenom_Client",
            "Date_heure_commande (JJ/MM/AAAA HH:MM)",
            "Debut_Reception (JJ/MM/AAAA HH:MM)", "Fin_Reception (JJ/MM/AAAA HH:MM)", "Mode_reception",
            "Point_de_vente", "Province_Livraison", "Adresse_Livraison", "Prix_Total" };

    private String[] commandeDetailExelHeader = { "Num_Commande", "Nom_produit", "Quantite", "Prix_Unitaire" };

    public ImportCommandeExelService(PointDeVenteService pointDeVenteService,
            ClientService clientService,
            ProvinceLivraisonService provinceLivraisonService,
            ProduitService produitService) {
        this.pointDeVenteService = pointDeVenteService;
        this.clientService = clientService;
        this.provinceLivraisonService = provinceLivraisonService;
        this.produitService = produitService;
    }

    public int getIndexOfPointDeVenteColumn() {
        for (int i = 0; i < commandeExelHeader.length; i++) {
            if (commandeExelHeader[i].equals("Point_de_vente")) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfModeReceptionColumn() {
        for (int i = 0; i < commandeExelHeader.length; i++) {
            if (commandeExelHeader[i].equals("Mode_reception")) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfQuantiteColumn() {
        int before = commandeExelHeader.length + 1;
        for (int i = 0; i < commandeDetailExelHeader.length; i++) {
            if (commandeDetailExelHeader[i].equals("Quantite")) {
                return i + before;
            }
        }
        return -1;
    }

    public String[] getCommandeExelHeader() {
        return commandeExelHeader;
    }

    public String[] getCommandeDetailExelHeader() {
        return commandeDetailExelHeader;
    }

    /*
     * public boolean isCommandeValid(Row row, Commandes commande) {
     * String nomClient = "";
     * String prenomClient = "";
     * LocalDateTime dateCommande = null;
     * for (int i = 0; i < commandeExelHeader.length; i++) {
     * Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
     * String cellValue = cell.toString().trim();
     * 
     * switch (i) {
     * case 0: // Num_commande
     * try {
     * commande.setId(Integer.valueOf(cellValue));
     * } catch (Exception e) {
     * return false;
     * }
     * break;
     * 
     * case 1: // Nom_client
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * return false;
     * nomClient = cellValue;
     * break;
     * 
     * case 2: // Prenom_Client
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * return false;
     * prenomClient = cellValue;
     * break;
     * 
     * case 3: // Date_heure_commande
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * return false;
     * LocalDateTime dateTime =
     * ImportExcelService.localDateTimeValidator(cellValue);
     * if(dateTime == null)
     * return false;
     * dateCommande = dateTime;
     * commande.setDatecommande(java.sql.Timestamp.valueOf(dateTime));
     * break;
     * 
     * case 4: // Debut_Reception
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * commande.setHeureReceptionDebut(java.sql.Timestamp.valueOf(dateCommande));
     * LocalDateTime dateTime2 =
     * ImportExcelService.localDateTimeValidator(cellValue);
     * commande.setHeureReceptionDebut(java.sql.Timestamp.valueOf(dateTime2));
     * if(dateTime2 == null)
     * return false;
     * break;
     * 
     * case 5: // Debut_Reception
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * commande.setHeureReceptionFin(java.sql.Timestamp.valueOf(dateCommande));
     * LocalDateTime dateTime3 =
     * ImportExcelService.localDateTimeValidator(cellValue);
     * commande.setHeureReceptionFin(java.sql.Timestamp.valueOf(dateTime3));
     * if(dateTime3 == null)
     * return false;
     * break;
     * 
     * case 6: // Mode_reception
     * if(cellValue.equals("") || cellValue.isBlank() || cellValue.isEmpty())
     * return false;
     * if(cellValue.equals("Retrait") ||
     * cellValue.toLowerCase().contains("retrait"))
     * commande.setModeReception(ModeReception.Retrait_Boutique);
     * else if (cellValue.equals("Livraison") ||
     * cellValue.toLowerCase().contains("livraison"))
     * commande.setModeReception(ModeReception.Livraison_Domicile);
     * else
     * return false;
     * break;
     * 
     * case 7 : // Point_de_vente
     * boolean emptyCell = cellValue.equals("") || cellValue.isBlank() ||
     * cellValue.isEmpty();
     * if(emptyCell &&
     * commande.getModeReception().equals(ModeReception.Retrait_Boutique))
     * return false;
     * PointDeVente ptv = pointDeVenteService.findPointDeVenteByNom(cellValue);
     * if (ptv == null)
     * return false;
     * commande.setPointDeVenteRetrait(ptv);
     * break;
     * 
     * case 8 : // Province_Livraison
     * emptyCell = cellValue.equals("") || cellValue.isBlank() ||
     * cellValue.isEmpty();
     * if(emptyCell &&
     * commande.getModeReception().equals(ModeReception.Livraison_Domicile))
     * return false;
     * ProvinceLivraison province =
     * provinceLivraisonService.findByWordInNom(cellValue);
     * if (province == null)
     * return false;
     * break;
     * 
     * case 9 : // Adresse_Livraison
     * emptyCell = cellValue.equals("") || cellValue.isBlank() ||
     * cellValue.isEmpty();
     * if(emptyCell &&
     * commande.getModeReception().equals(ModeReception.Livraison_Domicile))
     * return false;
     * commande.setAdresseLivraison(cellValue);
     * break;
     * 
     * default:
     * break;
     * }
     * }
     * 
     * Client client = clientService.searchClientsByNometPrenom(nomClient,
     * prenomClient).get(0);
     * if(client == null) {
     * client = new Client();
     * client.setNom(nomClient);
     * client.setPrenom(prenomClient);
     * client = clientService.saveClient(client);
     * }
     * 
     * commande.setClient(client);
     * 
     * return true;
     * }
     */

    public boolean isCommandeValid(Row row, Commandes commande) {
        String nomClient = "";
        String prenomClient = "";
        LocalDateTime dateCommande = null;

        for (int i = 0; i < commandeExelHeader.length; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String cellValue = cell.toString().trim();

            switch (i) {
                case 0: // Num_commande
                    try {
                        // Sécurité : On nettoie le ".0" d'Excel s'il existe
                        if (cell.getCellType() == CellType.NUMERIC) {
                            commande.setId((int) cell.getNumericCellValue());
                        } else {
                            if (cellValue.contains(".")) {
                                cellValue = cellValue.split("\\.")[0];
                            }
                            commande.setId(Integer.valueOf(cellValue));
                        }
                    } catch (Exception e) {
                        System.out.println(
                                "   -> [Échec Case 0] Erreur de conversion sur l'ID de commande. Valeur brute: '"
                                        + cell.toString() + "'");
                        return false;
                    }
                    break;

                case 1: // Nom_client
                    if (cellValue.isEmpty()) {
                        System.out.println("   -> [Échec Case 1] Le nom du client est vide.");
                        return false;
                    }
                    nomClient = cellValue;
                    break;

                case 2: // Prenom_Client
                    if (cellValue.isEmpty()) {
                        System.out.println("   -> [Échec Case 2] Le prénom du client est vide.");
                        return false;
                    }
                    prenomClient = cellValue;
                    break;

                case 3: // Date_heure_commande
                    if (cellValue.isEmpty()) {
                        System.out.println("   -> [Échec Case 3] La cellule Date est vide.");
                        return false;
                    }

                    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                        // C'est une vraie date Excel ! On la convertit directement en LocalDateTime
                        dateCommande = cell.getLocalDateTimeCellValue();
                    } else {
                        // C'est du texte brut, on passe par ton validateur habituel
                        dateCommande = ImportExcelService.localDateTimeValidator(cellValue);
                    }

                    if (dateCommande == null) {
                        System.out.println("   -> [Échec Case 3] Le validateur de date a échoué pour la valeur: '"
                                + cellValue + "'");
                        return false;
                    }
                    commande.setDatecommande(java.sql.Timestamp.valueOf(dateCommande));
                    break;

                case 4: // Debut_Reception
                    if (cellValue.isEmpty()) {
                        if (dateCommande != null) {
                            commande.setHeureReceptionDebut(java.sql.Timestamp.valueOf(dateCommande));
                        }
                    } else {
                        LocalDateTime dateTime2;
                        // Vérification si la cellule contient une date/heure stockée par Excel
                        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                            dateTime2 = cell.getLocalDateTimeCellValue();
                        } else {
                            dateTime2 = ImportExcelService.localDateTimeValidator(cellValue);
                        }

                        if (dateTime2 == null) {
                            System.out.println(
                                    "   -> [Échec Case 4] Validateur Heure Début invalide pour: '" + cellValue + "'");
                            return false;
                        }
                        commande.setHeureReceptionDebut(java.sql.Timestamp.valueOf(dateTime2));
                    }
                    break;

                case 5: // Fin_Reception
                    if (cellValue.isEmpty()) {
                        if (dateCommande != null) {
                            commande.setHeureReceptionFin(java.sql.Timestamp.valueOf(dateCommande));
                        }
                    } else {
                        LocalDateTime dateTime3;
                        // Vérification si la cellule contient une date/heure stockée par Excel
                        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                            dateTime3 = cell.getLocalDateTimeCellValue();
                        } else {
                            dateTime3 = ImportExcelService.localDateTimeValidator(cellValue);
                        }

                        if (dateTime3 == null) {
                            System.out.println(
                                    "   -> [Échec Case 5] Validateur Heure Fin invalide pour: '" + cellValue + "'");
                            return false;
                        }
                        commande.setHeureReceptionFin(java.sql.Timestamp.valueOf(dateTime3));
                    }
                    break;

                case 6: // Mode_reception
                    if (cellValue.isEmpty()) {
                        System.out.println("   -> [Échec Case 6] Le mode de réception est vide.");
                        return false;
                    }
                    if (cellValue.equalsIgnoreCase("Retrait") || cellValue.toLowerCase().contains("retrait")) {
                        commande.setModeReception(ModeReception.Retrait_Boutique);
                    } else if (cellValue.equalsIgnoreCase("Livraison")
                            || cellValue.toLowerCase().contains("livraison")) {
                        commande.setModeReception(ModeReception.Livraison_Domicile);
                    } else {
                        System.out.println("   -> [Échec Case 6] Valeur de réception inconnue: '" + cellValue + "'");
                        return false;
                    }
                    break;

                case 7: // Point_de_vente
                    if (cellValue.isEmpty() && commande.getModeReception() == ModeReception.Retrait_Boutique) {
                        System.out.println("   -> [Échec Case 7] Point de vente requis mais cellule vide.");
                        return false;
                    }
                    if (!cellValue.isEmpty()) {
                        PointDeVente ptv = pointDeVenteService.findPointDeVenteByNom(cellValue);
                        if (ptv == null && commande.getModeReception() == ModeReception.Retrait_Boutique) {
                            System.out.println(
                                    "   -> [Échec Case 7] Point de vente introuvable en BDD: '" + cellValue + "'");
                            return false;
                        }
                        commande.setPointDeVenteRetrait(ptv);
                    }
                    break;

                case 8: // Province_Livraison
                    if (cellValue.isEmpty() && commande.getModeReception() == ModeReception.Livraison_Domicile) {
                        System.out.println("   -> [Échec Case 8] Province requise pour livraison mais cellule vide.");
                        return false;
                    }
                    if (!cellValue.isEmpty()) {
                        ProvinceLivraison province = provinceLivraisonService.findByWordInNom(cellValue);
                        if (province == null && commande.getModeReception() == ModeReception.Livraison_Domicile) {
                            System.out.println("   -> [Échec Case 8] Province introuvable en BDD: '" + cellValue + "'");
                            return false;
                        }
                        commande.setProvinceLivraison(province);
                    }
                    break;

                case 9: // Adresse_Livraison
                    if (cellValue.isEmpty() && commande.getModeReception() == ModeReception.Livraison_Domicile) {
                        System.out.println("   -> [Échec Case 9] Adresse manquante pour la livraison.");
                        return false;
                    }
                    commande.setAdresseLivraison(cellValue);
                    break;

                default:
                    break;
            }
        }

        // Sauvegarde ou récupération du Client sécurisée
        try {
            var clientsTrouves = clientService.searchClientsByNometPrenom(nomClient, prenomClient);
            Client client = (clientsTrouves != null && !clientsTrouves.isEmpty()) ? clientsTrouves.get(0) : null;
            if (client == null) {
                client = new Client();
                client.setNom(nomClient);
                client.setPrenom(prenomClient);
                client = clientService.saveClient(client);
            }
            commande.setClient(client);
        } catch (Exception e) {
            System.out.println(
                    "   -> [Échec Client] Impossible de lier ou de créer le client: " + nomClient + " " + prenomClient);
            return false;
        }

        return true;
    }

    public boolean isCommandeDetailValid(Row row, int numCommande, DetailCommandeExelDto detailCommande) {
        int scale = commandeExelHeader.length + 1;

        for (int i = 0; i < commandeDetailExelHeader.length; i++) {
            int idx = i + scale;
            Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String cellValue = cell.toString().trim();

            switch (i) {
                case 0: // Num_Commande
                    try {
                        int numCmd;
                        if (cell.getCellType() == CellType.NUMERIC) {
                            numCmd = (int) cell.getNumericCellValue();
                        } else {
                            // Si c'est lu comme du texte (ex: "1.0"), on retire le ".0"
                            if (cellValue.contains(".")) {
                                cellValue = cellValue.split("\\.")[0];
                            }
                            numCmd = Integer.valueOf(cellValue);
                        }

                        if (numCmd != numCommande) {
                            System.out.println("   -> [Détail Rejeté] Num_Commande de la ligne (" + numCmd
                                    + ") ne correspond pas au Num_Commande attendu (" + numCommande + ")");
                            return false;
                        }
                        detailCommande.setNumCommande(numCmd);
                    } catch (Exception e) {
                        System.out.println(
                                "   -> [Détail Rejeté Case 0] Erreur de parsing du Num_Commande : " + cellValue);
                        return false;
                    }
                    break;

                case 1: // Nom_produit
                    if (cellValue.isEmpty()) {
                        System.out.println("   -> [Détail Rejeté Case 1] Nom de produit vide.");
                        return false;
                    }
                    Produit produit = produitService.findProduitByNom(cellValue);
                    if (produit == null) {
                        System.out.println(
                                "   -> [Détail Rejeté Case 1] Produit introuvable en BDD : '" + cellValue + "'");
                        return false;
                    }
                    detailCommande.setProduit(produit);
                    break;

                case 2: // Quantite
                    try {
                        int qte;
                        if (cell.getCellType() == CellType.NUMERIC) {
                            qte = (int) cell.getNumericCellValue();
                        } else {
                            if (cellValue.contains(".")) {
                                cellValue = cellValue.split("\\.")[0];
                            }
                            qte = Integer.valueOf(cellValue);
                        }
                        detailCommande.setQuantite(qte);
                    } catch (Exception e) {
                        System.out.println(
                                "   -> [Détail Rejeté Case 2] Erreur de parsing de la quantité : " + cellValue);
                        return false;
                    }
                    break;

                default:
                    break;
            }
        }

        return true;
    }
}
