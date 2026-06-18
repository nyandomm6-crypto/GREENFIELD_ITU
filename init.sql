-- Active: 1780380832087@@127.0.0.1@5432@bdctrlETU004353
CREATE DATABASE greenfield;

-- =====================================================================
-- 1. STRUCTURES DE BASE & SÉCURITÉ
-- =====================================================================

CREATE TABLE PointDeVente (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    contact VARCHAR(50) NOT NULL
);

-- Gestion fine des profils utilisateurs requis par la matrice des rôles (MVP)
CREATE TYPE f_role AS ENUM (
    'Client', 'Administrateur', 'Caissier', 'Livreur', 
    'Employé', 'Responsable Financier', 'RH', 'Manager'
);

CREATE TABLE Employes (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    contact VARCHAR(50),
    mail VARCHAR(150) UNIQUE NOT NULL,
    motdepasse VARCHAR(255) NOT NULL,
    role f_role NOT NULL, -- Matrice des rôles du cahier des charges
    idptdevente INT REFERENCES PointDeVente(id) ON DELETE SET NULL,
    est_actif BOOLEAN DEFAULT TRUE -- Pour l'archivage/CRUD simplifié
);

CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    contact VARCHAR(50),
    mail VARCHAR(150) UNIQUE NOT NULL,
    motdepasse VARCHAR(255) NOT NULL,
    soldefidelite DECIMAL(10, 2) DEFAULT 0.00 -- En Ar (Ariary)
);

-- =====================================================================
-- 2. FIDÉLITÉ (Valeurs fixes selon règles métiers MVP : 50 000 Ar, 10 cases)
-- =====================================================================

CREATE TABLE CarteFideliteClient (
    id SERIAL PRIMARY KEY,
    idclient INT REFERENCES Client(id) ON DELETE CASCADE,
    nombrecase INT DEFAULT 0, -- Incrémenté à chaque achat éligible (max 10)
    valeurparcase DECIMAL(10, 2) DEFAULT 5000.00, -- 5 000 Ar * 10 = 50 000 Ar
    valeurcarte DECIMAL(10, 2) DEFAULT 0.00, -- Devient 50 000 Ar quand validée
    dateAchat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateExpiration TIMESTAMP,
    est_valide BOOLEAN DEFAULT FALSE -- Devient TRUE après 10 achats
);

-- =====================================================================
-- 3. LOGISTIQUE & FLOTTE DE VÉHICULES (MVP V1)
-- =====================================================================

CREATE TYPE statut_vehicule AS ENUM ('Disponible', 'En course', 'En panne');

CREATE TABLE Vehicule (
    id SERIAL PRIMARY KEY,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    annee INT,
    capacite DECIMAL(10, 2), -- ex: en kg ou volume
    statut statut_vehicule DEFAULT 'Disponible'
    -- 'consommation par mois' supprimé -> Déplacé en Post-MVP (Suivi carburant)
);

-- =====================================================================
-- 4. PRODUCTION (Nouveautés strictes exigées par le MVP V1)
-- =====================================================================

CREATE TABLE CollecteDechets (
    id SERIAL PRIMARY KEY,
    partenaire VARCHAR(150) NOT NULL,
    poids_volume DECIMAL(10, 2) NOT NULL, -- Suivi quantitatif requis
    date_collecte TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE etape_maturation AS ENUM ('Collecté', 'En fermentation', 'Retournement', 'Tamisage', 'Prêt');

CREATE TABLE LotCompost (
    id SERIAL PRIMARY KEY,
    code_lot VARCHAR(50) UNIQUE NOT NULL,
    quantite_produite DECIMAL(10, 2) NOT NULL,
    etape_actuelle etape_maturation DEFAULT 'Collecté', -- Mise à jour manuelle exigée
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_employe_responsable INT REFERENCES Employes(id)
);

-- =====================================================================
-- 5. PRODUITS, STOCKS & TRANSFERTS
-- =====================================================================

CREATE TABLE Produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    pu DECIMAL(10, 2) NOT NULL, -- Prix unitaire en Ar
    categorie VARCHAR(50) -- ex: 'Compost', 'Plante'
);

-- Mouvement centralisé (Unité centrale, distribution, ventes)
CREATE TYPE type_mvt AS ENUM ('Entree_Production', 'Sortie_Transfert', 'Entree_Boutique', 'Vente_Client', 'Perte');

CREATE TABLE MvtStock (
    id SERIAL PRIMARY KEY,
    type_mouvement type_mvt NOT NULL,
    idproduit INT REFERENCES Produit(id) ON DELETE CASCADE,
    idptdevente INT REFERENCES PointDeVente(id), -- NULL si c'est l'unité centrale
    quantite INT NOT NULL,
    dateMvt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
-- 6. COMMANDES, RETRAITS & LIVRAISONS
-- =====================================================================

CREATE TYPE type_payement AS ENUM ('Espece', 'Mobile_Money', 'Carte_Fidelite');
CREATE TYPE mode_reception AS ENUM ('Retrait_Boutique', 'Livraison_Domicile');
CREATE TYPE statut_livraison AS ENUM ('En attente', 'En cours', 'Livré', 'Annulé');

CREATE TABLE Commandes (
    id SERIAL PRIMARY KEY,
    idclient INT REFERENCES Client(id) ON DELETE SET NULL,
    datecommande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode_reception mode_reception NOT NULL,
    idptdevente_retrait INT REFERENCES PointDeVente(id), -- Si Retrait sur place
    adresse_livraison VARCHAR(255), -- Si Livraison à domicile
    plage_horaire_souhaitee VARCHAR(100), 
    typepayement type_payement NOT NULL,
    frais_livraison DECIMAL(10, 2) DEFAULT 0.00, -- 0 Ar si total >= 200 000 Ar
    total_produits DECIMAL(10, 2) NOT NULL,
    total_general DECIMAL(10, 2) NOT NULL -- total_produits + frais_livraison
);

CREATE TABLE DetailsCommande (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes(id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit(id),
    quantite INT NOT NULL,
    pu_au_moment_achat DECIMAL(10, 2) NOT NULL -- Évite les ruptures d'historique en cas de changement de PU
);

CREATE TABLE Livraison (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes(id) ON DELETE CASCADE,
    idvehicule INT REFERENCES Vehicule(id) ON DELETE SET NULL,
    idlivreur INT REFERENCES Employes(id) ON DELETE SET NULL, -- Affectation directe exigée
    dateLivraison TIMESTAMP,
    statutLivraison statut_livraison DEFAULT 'En attente'
);

-- =====================================================================
-- 7. MARKETING, PROMOTIONS & OBJECTIFS (MVP V1)
-- =====================================================================

CREATE TABLE Promotion (
    id SERIAL PRIMARY KEY,
    idProduit INT REFERENCES Produit(id) ON DELETE CASCADE,
    pourcentage INT CHECK (pourcentage BETWEEN 1 AND 100),
    dateDebut TIMESTAMP NOT NULL,
    dateFin TIMESTAMP NOT NULL
);

CREATE TABLE Objectif (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL,
    idProduit INT REFERENCES Produit(id) ON DELETE SET NULL, -- NULL si objectif global sur tout type de produit
    quantite_cible INT NOT NULL,
    datedebut TIMESTAMP NOT NULL,
    datefin TIMESTAMP NOT NULL
);

CREATE TABLE ObjectifPtDeVente (
    id SERIAL PRIMARY KEY,
    idptvente INT REFERENCES PointDeVente(id) ON DELETE CASCADE,
    idobjectif INT REFERENCES Objectif(id) ON DELETE CASCADE,
    atteint BOOLEAN DEFAULT FALSE -- Si TRUE, déclenche le calcul de prime collective
);

-- =====================================================================
-- 8. FLUX DE TRÉSORERIE DE BASE (MVP V1)
-- =====================================================================

CREATE TYPE type_flux AS ENUM ('Entree_Vente', 'Depense_Exploitation');

CREATE TABLE Tresorerie (
    id SERIAL PRIMARY KEY,
    type_mouvement type_flux NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    date_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT, -- Saisie manuelle pour les dépenses d'exploitation
    idcommande INT REFERENCES Commandes(id) -- Lié automatiquement si c'est une vente
);