CREATE DATABASE greenfield;

CREATE TYPE type_message AS ENUM ( 'DemandeStock', 'Autre');

CREATE TYPE f_role AS ENUM (
    'Client', 'Administrateur', 'Caissier', 'Livreur', 
    'Employé', 'Responsable Financier', 'RH', 'Manager'
);

CREATE TYPE statut_vehicule AS ENUM ('Disponible', 'En course', 'En panne');

CREATE TYPE type_mvt AS ENUM ('Entree_Production', 'Sortie_Transfert', 'Entree_Boutique', 'Vente_Client', 'Perte');

CREATE TYPE type_payement AS ENUM ('Espece', 'Mobile_Money', 'Carte_Fidelite');

CREATE TYPE mode_reception AS ENUM ('Retrait_Boutique', 'Livraison_Domicile');

CREATE TYPE statut_livraison AS ENUM ('En attente', 'En cours', 'Livré', 'Annulé');

CREATE TYPE statut_commande AS ENUM ('Cree','En cours', 'Paye', 'Annulé');

CREATE TYPE type_flux AS ENUM ('Entree_Vente', 'Depense_Exploitation');

CREATE TYPE statut_transfert AS ENUM ('Cree', 'En cours', 'Terminé');

CREATE TABLE PointDeVente (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    adresse VARCHAR(255) NOT NULL,
    contact VARCHAR(50) NOT NULL
);

CREATE Table Notifications (
    id SERIAL PRIMARY KEY,
    typeMessage type_message,
    objet VARCHAR(100),
    message TEXT,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    idDemandeStock INT DEFAULT NULL,
    date TIMESTAMP DEFAULT,
    envoyeur BOOLEAN DEFAULT true
);

CREATE TABLE DemandeStock (
    id SERIAL PRIMARY KEY,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    dateDemande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateCible TIMESTAMP DEFAULT
);

CREATE Table DemandeStockFille (
    id SERIAL PRIMARY KEY,
    idDemandeStock INT REFERENCES DemandeStock (id) ON DELETE SET NULL,
    idproduit INT REFERENCES Produit (id) ON DELETE CASCADE
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
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code) ON DELETE SET NULL,
    est_actif BOOLEAN DEFAULT TRUE -- Pour l'archivage/CRUD simplifié
);

CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    contact VARCHAR(50),
    mail VARCHAR(150) UNIQUE NOT NULL,
    motdepasse VARCHAR(255) NOT NULL
);

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

CREATE TABLE Produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    pu DECIMAL(10, 2) NOT NULL, -- Prix unitaire en Ar
    idcategorie INT REFERENCES CategorieProduit (id)
);

CREATE Table CategorieProduit (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE MvtStock (
    id SERIAL PRIMARY KEY,
    type_mouvement type_mvt NOT NULL,
    idproduit INT REFERENCES Produit (id) ON DELETE CASCADE,
    idptdevente VARCHAR(20) REFERENCES PointDeVente (code), -- NULL si c'est l'unité centrale
    quantite INT NOT NULL,
    dateMvt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Commandes (
    id SERIAL PRIMARY KEY,
    idclient INT REFERENCES Client (id) ON DELETE SET NULL,
    datecommande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode_reception mode_reception NOT NULL,
    idptdevente_retrait VARCHAR(50) REFERENCES PointDeVente (code), -- Si Retrait sur place
    adresse_livraison VARCHAR(255), -- Si Livraison à domicile
    plage_horaire_souhaitee VARCHAR(100),
    statutCommande statut_commande NOT NULL DEFAULT 'En cours',
    frais_livraison DECIMAL(10, 2) DEFAULT 0.00, -- 0 Ar si total >= 200 000 Ar
    total_produits DECIMAL(10, 2) NOT NULL,
    total_general DECIMAL(10, 2) NOT NULL -- total_produits + frais_livraison
);

CREATE TABLE DetailsCommande (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id),
    quantite INT NOT NULL,
    pu_au_moment_achat DECIMAL(10, 2) NOT NULL -- Évite les ruptures d'historique en cas de changement de PU
);

CREATE Table Paiement (
    id SERIAL PRIMARY KEY,
    idcommande INT REFERENCES Commandes (id) ON DELETE CASCADE
);

CREATE Table PaiementFille (
    id SERIAL PRIMARY KEY,
    idPaiement INT REFERENCES Paiement (id) ON DELETE CASCADE,
    typePayement type_payement NOT NULL,
    valeur DECIMAL(10, 2)
);

CREATE TABLE Livraison (
    id SERIAL PRIMARY KEY,
    idvehicule INT REFERENCES Vehicule (id) ON DELETE SET NULL,
    idlivreur INT REFERENCES Employes (id) ON DELETE SET NULL, -- Affectation directe exigée
    dateLivraison TIMESTAMP,
    statutLivraison statut_livraison DEFAULT 'En attente'
);

CREATE Table LivraisonFille (
    id SERIAL PRIMARY KEY,
    idLivraison INT REFERENCES Livraison (id) ON DELETE SET NULL,
    idCommande INT REFERENCES Commandes (id) ON DELETE SET NULL,
    statutLivraisonFille statut_livraison DEFAULT 'En attente'
);

CREATE TABLE Tresorerie (
    id SERIAL PRIMARY KEY,
    type_mouvement type_flux NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    date_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT, -- Saisie manuelle pour les dépenses d'exploitation
    idcommande INT REFERENCES Commandes (id) -- Lié automatiquement si c'est une vente
);

CREATE TABLE Transferts (
    id SERIAL PRIMARY KEY,
    idPointDeVente VARCHAR(20) REFERENCES PointDeVente (code),
    idPointDeVenteCible VARCHAR(20) REFERENCES PointDeVente (code),
    statut_transfert statut_transfert NOT NULL,
    date_transfert TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TransfertsFille (
    id SERIAL PRIMARY KEY,
    idTransfert INT REFERENCES Transferts (id) ON DELETE CASCADE,
    idproduit INT REFERENCES Produit (id),
    quantite INT NOT NULL
)

-- =====================================================================
-- 4. PRODUCTION (Nouveautés strictes exigées par le MVP V1)
-- =====================================================================

-- CREATE TABLE CollecteDechets (
--     id SERIAL PRIMARY KEY,
--     partenaire VARCHAR(150) NOT NULL,
--     poids_volume DECIMAL(10, 2) NOT NULL, -- Suivi quantitatif requis
--     date_collecte TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- CREATE TYPE etape_maturation AS ENUM ('Collecté', 'En fermentation', 'Retournement', 'Tamisage', 'Prêt');

-- CREATE TABLE LotCompost (
--     id SERIAL PRIMARY KEY,
--     code_lot VARCHAR(50) UNIQUE NOT NULL,
--     quantite_produite DECIMAL(10, 2) NOT NULL,
--     etape_actuelle etape_maturation DEFAULT 'Collecté', -- Mise à jour manuelle exigée
--     date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     id_employe_responsable INT REFERENCES Employes(id)
-- );

-- =====================================================================
-- 5. PRODUITS, STOCKS & TRANSFERTS
-- =====================================================================