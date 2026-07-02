-- Active: 1780380832087@@127.0.0.1@5432@greenfield
CREATE DATABASE greenfield;

-- Se connecter à la base greenfield avant d'exécuter la suite
\c greenfield
-- =====================================================
-- TYPES ENUM
-- =====================================================

CREATE TYPE type_message AS ENUM (
    'DemandeStock',
    'Autre'
);

CREATE TYPE f_role AS ENUM (
    'Client',
    'Administrateur',
    'Caissier',
    'Livreur',
    'Employe',
    'Responsable_Financier',
    'RH',
    'Manager'
);

CREATE TYPE statut_vehicule AS ENUM (
    'Disponible',
    'En_course',
    'En_panne'
);

CREATE TYPE type_mvt AS ENUM (
    'Entree_Production',
    'Sortie_Transfert',
    'Entree_Boutique',
    'Vente_Client',
    'Perte'
);

CREATE TYPE type_payement AS ENUM (
    'Espece',
    'Mobile_Money',
    'Carte_Fidelite'
);

CREATE TYPE mode_reception AS ENUM (
    'Retrait_Boutique',
    'Livraison_Domicile'
);

CREATE TYPE statut_livraison AS ENUM (
    'En_attente',
    'En_cours',
    'Livre',
    'Annule'
);

CREATE TYPE type_flux AS ENUM (
    'Entree_Vente',
    'Depense_Exploitation'
);

CREATE TYPE statut_transfert AS ENUM (
    'Cree',
    'En_cours',
    'Termine'
);

CREATE TYPE statut_paiement AS ENUM (
    'Cree',
    'Reste',
    'Cloture'
);

CREATE TYPE type_commande AS ENUM (
    'En ligne',
    'En boutique'
);

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
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE Produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    pu DECIMAL(10, 2) NOT NULL,
    idcategorie INT REFERENCES CategorieProduit (id) ON DELETE SET NULL
);

ALTER TABLE Produit ADD COLUMN poids_moyenne_unitaire DECIMAL(10, 2) DEFAULT 0;

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
    role f_role NOT NULL,
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
    mail VARCHAR(150) UNIQUE NOT NULL,
    motdepasse VARCHAR(255) NOT NULL,
    estVerifier BOOLEAN DEFAULT FALSE,
    date DATE DEFAULT CURRENT_DATE
);

ALTER TABLE client ALTER COLUMN mail DROP NOT NULL;
ALTER TABLE client ALTER COLUMN motdepasse DROP NOT NULL;
ALTER TABLE client ALTER COLUMN estverifier DROP NOT NULL;

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
    statut statut_vehicule DEFAULT 'Disponible',
    date DATE DEFAULT CURRENT_DATE
);

-- =====================================================
-- MOUVEMENTS DE STOCK
-- =====================================================

CREATE TABLE MvtStock (
    id SERIAL PRIMARY KEY,
    type_mouvement type_mvt NOT NULL,
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
---DROP TYPE statut_commande;

CREATE TABLE statutcommande (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(60)
);

CREATE TABLE Commandes (
    id SERIAL PRIMARY KEY,
    idclient INT REFERENCES Client (id) ON DELETE SET NULL,
    datecommande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode_reception varchar(30),
    idptdevente_retrait VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    adresse_livraison VARCHAR(255),
    type_commande VARCHAR(30) DEFAULT 'En boutique',
    statutActuel INT REFERENCES statutcommande (id) ON DELETE SET NULL DEFAULT 1,
    heure_reception_debut TIMESTAMP,
    heure_reception_fin TIMESTAMP,
    frais_livraison DECIMAL(10, 2) DEFAULT 0,
    total_produits INT NOT NULL,
    total_general DECIMAL(10, 2) NOT NULL,
    poids_total DECIMAL(10, 2) DEFAULT 0
);

-- ALTER TABLE Commandes ALTER COLUMN mode_reception TYPE varchar(30);
-- ALTER TABLE Commandes ADD COLUMN type_commande VARCHAR(30) DEFAULT 'En boutique';
-- ALTER TABLE Commandes ADD COLUMN heure_reception_debut TIMESTAMP;
-- ALTER TABLE Commandes ADD COLUMN heure_reception_fin TIMESTAMP;

-- ALTER TABLE commandes DROP COLUMN statutCommande;

-- ALTER TABLE commandes ADD COLUMN statutActuel INT REFERENCES statutcommande (id) ON DELETE SET NULL DEFAULT 1;
-- ALTER TABLE commandes ADD COLUMN provinceLivraisonId INT REFERENCES provinceLivraison (id) ON DELETE SET NULL;

-- ALTER TABLE commandes ADD COLUMN poids_total DECIMAL(10, 2) DEFAULT 0;



CREATE TABLE histstatutcommande (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES commandes (id),
    idstatut INT REFERENCES statutcommande(id),
    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--ALTER TABLE Commandes ALTER COLUMN total_produits TYPE INT;

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
    statut statut_paiement DEFAULT 'Cree',
    date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE PaiementFille (
    id SERIAL PRIMARY KEY,
    idPaiement INT REFERENCES Paiement (id) ON DELETE CASCADE,
    typePayement type_payement NOT NULL,
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
    statutLivraison statut_livraison DEFAULT 'En_attente',
    date DATE DEFAULT CURRENT_DATE
);

--drop table livraison;

CREATE TABLE LivraisonFille (
    id SERIAL PRIMARY KEY,
    idLivraison INT REFERENCES Livraison (id) ON DELETE CASCADE,
    idCommande INT REFERENCES Commandes (id) ON DELETE CASCADE,
    statutLivraisonFille statut_livraison DEFAULT 'En_attente'
);

-- =====================================================
-- TRESORERIE
-- =====================================================

CREATE TABLE Tresorerie (
    id SERIAL PRIMARY KEY,
    type_mouvement type_flux NOT NULL,
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
    statut_transfert statut_transfert NOT NULL,
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
    typeMessage type_message NOT NULL,
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

ALTER TABLE livraison ALTER COLUMN statutlivraison TYPE varchar(30);

ALTER TABLE LivraisonFille
ALTER COLUMN statutLivraisonFille TYPE varchar(30);