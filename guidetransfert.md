transfert
id point de vente qui envoye une demande de transfer et le id point de vente recupere le transfert
si le stock du point de vente est epuise alors il vend les produit qui est demande par le transfert 


src/main/java/itu/GreenField/
├── GreenFieldApplication.java          # Point d'entrée Spring Boot
├── controller/                         # Contrôleurs REST
│   └── TransfertController.java        # API des transferts
├── service/                            # Logique métier
│   ├── TransfertService.java           # Service principal des transferts
│   ├── EnvoiEmail.java                 # Envoi d'emails
│   ├── ValidationMailService.java      # Validation des emails
│   ├── ValidationService.java          # Services de validation
│   └── UtilsService.java               # Utilitaires
├── repository/                         # Accès base de données
│   ├── TransfertsRepository.java       # CRUD transferts
│   ├── TransfertsFilleRepository.java  # CRUD lignes transferts
│   ├── StockRepository.java            # Gestion des stocks
│   ├── MvtStockRepository.java         # Mouvements de stock
│   ├── MvtStockFilleRepository.java    # Lignes mouvements
│   ├── PointDeVenteRepository.java     # Points de vente
│   ├── ProduitRepository.java          # Produits
│   ├── DemandeStockRepository.java     # Demandes de stock
│   └── DemandeStockFilleRepository.java # Lignes demandes
├── model/                              # Entités JPA
│   ├── Transferts.java                 # Entité transfert
│   ├── TransfertsFille.java            # Ligne de transfert
│   ├── PointDeVente.java               # Point de vente
│   ├── Produit.java                    # Produit
│   ├── StatutTransfert.java            # Énumération statuts
│   └── ... (autres entités)
└── dto/                                # Objets de transfert
    ├── DemandeTransfertRequest.java    # DTO demande transfert
    ├── CreerTransfertRequest.java      # DTO création transfert
    ├── ProduitQuantiteDTO.java         # DTO produit+quantité
    └── TransfertDetailResponse.java    # DTO réponse détail


