-- Active: 1782932337041@@127.0.0.1@5433@final_gr
CREATE DATABASE gf;

-- Se connecter à la base greenfield avant d'exécuter la suite
-- =====================================================
-- TABLES DE BASE
-- =====================================================

CREATE TABLE PointDeVente (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    contact VARCHAR(50) NOT NULL
);

CREATE TABLE CategorieProduit (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL,
    image VARCHAR(255)
);

CREATE TABLE Produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    pu DECIMAL(10, 2) NOT NULL,
    description TEXT,
    idcategorie INT REFERENCES CategorieProduit (id) ON DELETE SET NULL,
    poids_moyenne_unitaire DECIMAL(10, 2) DEFAULT 0
);

-- =====================================================
-- DEMANDES DE STOCK
-- =====================================================

CREATE TABLE DemandeStock (
    id SERIAL PRIMARY KEY,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    dateDemande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateCible TIMESTAMP
);

CREATE TABLE DemandeStockFille (
    id SERIAL PRIMARY KEY,
    idDemandeStock INT REFERENCES DemandeStock (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id) ON DELETE CASCADE,
    quantite INT
);

-- ====================================================
-- EMPLOYES
-- =====================================================

CREATE TABLE Employes (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    contact VARCHAR(50),
    mail VARCHAR(150) UNIQUE NOT NULL,
    motdepasse VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    est_actif BOOLEAN DEFAULT TRUE,
    date DATE DEFAULT CURRENT_DATE
);

-- =====================================================
-- CLIENTS
-- =====================================================

CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    contact VARCHAR(50),
    mail VARCHAR(150),
    motdepasse VARCHAR(255),
    estVerifier BOOLEAN DEFAULT FALSE,
    date DATE DEFAULT CURRENT_DATE
);

-- =====================================================
-- VEHICULES
-- =====================================================

CREATE TABLE Vehicule (
    id SERIAL PRIMARY KEY,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    annee INT,
    capacite DECIMAL(10, 2),
    statut VARCHAR(50) DEFAULT 'Disponible',
    date DATE DEFAULT CURRENT_DATE
);

-- =====================================================
-- MOUVEMENTS DE STOCK
-- =====================================================

CREATE TABLE MvtStock (
    id SERIAL PRIMARY KEY,
    type_mouvement VARCHAR(50) NOT NULL,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    dateMvt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE MvtStockFille (
    id SERIAL PRIMARY KEY,
    idMvtStock INT REFERENCES MvtStock (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id) ON DELETE RESTRICT,
    quantite INT NOT NULL
);

-- =====================================================
-- COMMANDES
-- =====================================================

CREATE TABLE provinceLivraison (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

CREATE TABLE fraisLivraison (
    id SERIAL PRIMARY KEY,
    idprovince INT REFERENCES provinceLivraison (id) ON DELETE SET NULL,
    poidsreference DECIMAL(10, 2) NOT NULL,
    montant DECIMAL(10, 2) NOT NULL
);

CREATE TABLE statutcommande (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(60)
);

CREATE TABLE Commandes (
    id SERIAL PRIMARY KEY,
    idclient INT REFERENCES Client (id) ON DELETE SET NULL,
    datecommande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode_reception VARCHAR(50),
    idptdevente_retrait VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    adresse_livraison VARCHAR(255),
    type_commande VARCHAR(50) DEFAULT 'En boutique',
    statutActuel INT REFERENCES statutcommande (id) ON DELETE SET NULL DEFAULT 1,
    provinceLivraisonId INT REFERENCES provinceLivraison (id) ON DELETE SET NULL,
    heure_reception_debut TIMESTAMP,
    heure_reception_fin TIMESTAMP,
    frais_livraison DECIMAL(10, 2) DEFAULT 0,
    total_produits INT NOT NULL,
    total_general DECIMAL(10, 2) NOT NULL,
    poids_total DECIMAL(10, 2) DEFAULT 0
);

CREATE TABLE histstatutcommande (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES commandes (id),
    idstatut INT REFERENCES statutcommande (id),
    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE DetailsCommande (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id) ON DELETE RESTRICT,
    quantite INT NOT NULL,
    pu_au_moment_achat DECIMAL(10, 2) NOT NULL
);

-- =====================================================
-- PAIEMENTS
-- =====================================================

CREATE TABLE Paiement (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes (id) ON DELETE CASCADE,
    statut VARCHAR(50) DEFAULT 'Cree',
    date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE PaiementFille (
    id SERIAL PRIMARY KEY,
    idPaiement INT REFERENCES Paiement (id) ON DELETE CASCADE,
    typePayement VARCHAR(50) NOT NULL,
    valeur DECIMAL(10, 2) NOT NULL
);

-- =====================================================
-- LIVRAISONS
-- =====================================================

CREATE TABLE Livraison (
    id SERIAL PRIMARY KEY,
    idvehicule INT REFERENCES Vehicule (id) ON DELETE SET NULL,
    idlivreur INT REFERENCES Employes (id) ON DELETE SET NULL,
    dateLivraison TIMESTAMP,
    statutLivraison VARCHAR(50) DEFAULT 'En_attente',
    date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE LivraisonFille (
    id SERIAL PRIMARY KEY,
    idLivraison INT REFERENCES Livraison (id) ON DELETE CASCADE,
    idCommande INT REFERENCES Commandes (id) ON DELETE CASCADE,
    statutLivraisonFille VARCHAR(50) DEFAULT 'En_attente'
);

-- =====================================================
-- TRESORERIE
-- =====================================================

CREATE TABLE Tresorerie (
    id SERIAL PRIMARY KEY,
    type_mouvement VARCHAR(50) NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    date_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    idcommande INT REFERENCES Commandes (id) ON DELETE SET NULL
);

-- =====================================================
-- TRANSFERTS
-- =====================================================

CREATE TABLE Transferts (
    id SERIAL PRIMARY KEY,
    idPointDeVente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    idPointDeVenteCible VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    statut_transfert VARCHAR(50) NOT NULL,
    date_transfert TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TransfertsFille (
    id SERIAL PRIMARY KEY,
    idTransfert INT REFERENCES Transferts (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id) ON DELETE RESTRICT,
    quantite INT NOT NULL
);

-- =====================================================
-- NOTIFICATIONS
-- =====================================================

CREATE TABLE Notifications (
    id SERIAL PRIMARY KEY,
    typeMessage VARCHAR(50) NOT NULL,
    objet VARCHAR(100),
    message TEXT,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    idDemandeStock INT REFERENCES DemandeStock (id) ON DELETE SET NULL,
    dateNotification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    envoyeur BOOLEAN DEFAULT TRUE
);

CREATE TABLE validation_mail (
    id SERIAL PRIMARY KEY,
    id_client INT REFERENCES client (id) ON DELETE CASCADE,
    token VARCHAR(5),
    est_verifie BOOLEAN DEFAULT FALSE,
    date_expiration TIMESTAMP NOT NULL
);

CREATE TABLE Panier (
    id SERIAL PRIMARY KEY,
    idClient INT REFERENCES Client (id) ON DELETE CASCADE,
    tokenSession VARCHAR(100) UNIQUE,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PanierFille (
    id SERIAL PRIMARY KEY,
    idPanier INT REFERENCES Panier (id) ON DELETE CASCADE,
    idProduit INT REFERENCES Produit (id) ON DELETE CASCADE,
    quantite INT NOT NULL
);

ALTER TABLE paiementfille ADD COLUMN date DATE DEFAULT CURRENT_DATE;

-- =====================================================
-- PHOTOS DE PRODUIT
-- =====================================================

CREATE TABLE IF NOT EXISTS photo (
    id SERIAL PRIMARY KEY,
    idproduit INT NOT NULL REFERENCES Produit (id) ON DELETE CASCADE,
    path VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS faq (
    id SERIAL PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    reponse TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INTEGER NOT NULL DEFAULT 0
);

INSERT INTO
    faq (
        question,
        reponse,
        active,
        ordre
    )
VALUES (
        'Comment se passe la livraison ?',
        'Nous livrons dans toute l''île en 24 à 48 heures.',
        true,
        1
    ),
    (
        'Quels moyens de paiement acceptez-vous ?',
        'MVola, Orange Money, Airtel Money et carte bancaire.',
        true,
        2
    );

CREATE TABLE IF NOT EXISTS avis_produit (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_client INTEGER NOT NULL,
    id_produit INTEGER NOT NULL,
    nom_client VARCHAR(150) NOT NULL,
    note INTEGER NOT NULL,
    commentaire TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avis_client FOREIGN KEY (id_client) REFERENCES client (id),
    CONSTRAINT fk_avis_produit FOREIGN KEY (id_produit) REFERENCES produit (id)
);

CREATE TABLE IF NOT EXISTS temoignage (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_client INTEGER,
    nom VARCHAR(150) NOT NULL,
    poste VARCHAR(150) NOT NULL DEFAULT 'client',
    message TEXT NOT NULL,
    note INTEGER NOT NULL DEFAULT 5,
    is_actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_temoignage_client FOREIGN KEY (id_client) REFERENCES client (id)
);

CREATE TABLE IF NOT EXISTS feature (
    id BIGSERIAL PRIMARY KEY,
    icon VARCHAR(100) NOT NULL,
    titre VARCHAR(150) NOT NULL,
    description VARCHAR(255) NOT NULL,
    section VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS publicite (
    id BIGSERIAL PRIMARY KEY,
    image_path VARCHAR(255),
    titre VARCHAR(150) NOT NULL,
    sous_titre VARCHAR(150),
    lien VARCHAR(255),
    class_div VARCHAR(255),
    class_content VARCHAR(255),
    class_titre VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS banniere (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    sous_titre VARCHAR(150),
    description VARCHAR(500) NOT NULL,
    image_path VARCHAR(255),
    lien VARCHAR(255),
    btn_texte VARCHAR(80),
    promo_nombre VARCHAR(50),
    promo_prix VARCHAR(50),
    promo_unite VARCHAR(50)
);

ALTER TABLE Produit ADD COLUMN description TEXT;