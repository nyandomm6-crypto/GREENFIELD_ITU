-- Active: 1780380832087@@127.0.0.1@5432@greenfield
-- =====================================================
-- DONNÉES DE TEST - GREENFIELD
-- Exécuter après avoir créé les tables (table.sql)
-- =====================================================

-- =====================================================
-- 1. POINTS DE VENTE
-- =====================================================
INSERT INTO PointDeVente (nom, code, adresse, contact) VALUES
    ('Boutique Centrale Antananarivo', 'BCA001', 'Analakely, Antananarivo', '034 12 345 67'),
    ('Boutique Nord Tana',             'BNT002', 'Ambohijanaka, Tana',      '033 98 765 43'),
    ('Boutique Toamasina',             'BTM003', 'Bazar Be, Toamasina',     '032 55 111 22'),
    ('Boutique Fianarantsoa',          'BFR004', 'Centre-ville, Fianarantsoa','034 77 888 99');

-- =====================================================
-- 2. CATÉGORIES DE PRODUITS
-- =====================================================
INSERT INTO CategorieProduit (libelle) VALUES
    ('Compost'),
    ('Engrais Bio'),
    ('Pellets Organiques'),
    ('Biogaz'),
    ('Fromage');

-- =====================================================
-- 3. PRODUITS
-- =====================================================
INSERT INTO Produit (nom, matricule, pu, idcategorie) VALUES
    ('Compost Premium 5kg',         'PRD-001', 12500.00,  1),
    ('Compost Standard 10kg',       'PRD-002', 20000.00,  1),
    ('Engrais Liquide Bio 1L',      'PRD-003', 8500.00,   2),
    ('Engrais Granulé Bio 2kg',     'PRD-004', 15000.00,  2),
    ('Pellets Chauffage 25kg',      'PRD-005', 35000.00,  3),
    ('Pellets Industriels 50kg',    'PRD-006', 60000.00,  3),
    ('Bouteille Biogaz 5L',         'PRD-007', 45000.00,  4),
    ('Bouteille Biogaz 10L',        'PRD-008', 85000.00,  4),
    ('Fromage Bio Fermier 250g',    'PRD-009', 9000.00,   5),
    ('Fromage Fondu Organic 200g',  'PRD-010', 7500.00,   5);

-- =====================================================
-- 4. EMPLOYÉS (mot de passe: "greenfield2025" en clair pour test)
-- =====================================================
INSERT INTO Employes (nom, prenom, adresse, contact, mail, motdepasse, role, idptdevente, est_actif) VALUES
    ('RAKOTO',   'Jean',    'Analakely, Tana',         '034 11 222 33', 'jean.rakoto@greenfield.mg',    '$2a$10$abc123hashedpassword', 'Administrateur',       'BCA001', TRUE),
    ('RASOA',    'Marie',   'Ambohijanaka, Tana',       '033 44 555 66', 'marie.rasoa@greenfield.mg',    '$2a$10$abc123hashedpassword', 'Manager',              'BCA001', TRUE),
    ('ANDRIANA', 'Paul',    'Bazar Be, Toamasina',      '032 77 888 99', 'paul.andriana@greenfield.mg',  '$2a$10$abc123hashedpassword', 'Caissier',             'BTM003', TRUE),
    ('RABE',     'Sophie',  'Centre, Fianarantsoa',     '034 00 111 22', 'sophie.rabe@greenfield.mg',    '$2a$10$abc123hashedpassword', 'Livreur',              'BFR004', TRUE),
    ('RAMIALY',  'Thierry', 'Ampefiloha, Tana',         '033 33 444 55', 'thierry.ramialy@greenfield.mg','$2a$10$abc123hashedpassword', 'Responsable_Financier','BCA001', TRUE),
    ('TSIRY',    'Lalao',   'Mahamasina, Tana',         '032 66 777 88', 'lalao.tsiry@greenfield.mg',    '$2a$10$abc123hashedpassword', 'RH',                   'BNT002', TRUE),
    ('VOLA',     'Ranto',   'Talatamaty, Tana',         '034 99 000 11', 'ranto.vola@greenfield.mg',     '$2a$10$abc123hashedpassword', 'Employe',              'BNT002', TRUE),
    ('NAINA',    'Fidy',    'Amboniloha, Tana',         '033 22 333 44', 'fidy.naina@greenfield.mg',     '$2a$10$abc123hashedpassword', 'Livreur',              'BCA001', TRUE);

-- =====================================================
-- 5. CLIENTS
-- =====================================================
INSERT INTO Client (nom, prenom, adresse, contact, mail, motdepasse, estVerifier) VALUES
    ('RANDRIA',    'Hery',       'Itaosy, Tana',            '034 10 200 30', 'hery.randria@mail.mg',     '$2a$10$clientpasshash', TRUE),
    ('MAHEFA',     'Zo',         'Ambohipo, Tana',           '033 40 500 60', 'zo.mahefa@mail.mg',        '$2a$10$clientpasshash', TRUE),
    ('RAKOTONDR',  'Aina',       'Anosibe, Tana',            '032 70 800 90', 'aina.rakotondr@mail.mg',   '$2a$10$clientpasshash', TRUE),
    ('SOLO',       'Faniry',     'Ankadifotsy, Tana',        '034 01 102 03', 'faniry.solo@mail.mg',      '$2a$10$clientpasshash', TRUE),
    ('TIANA',      'Noro',       'Mahazo, Tana',             '033 41 502 63', 'noro.tiana@mail.mg',       '$2a$10$clientpasshash', TRUE),
    ('BEMA',       'Lovatiana',  'Toamasina Centre',         '032 71 803 93', 'lovatiana.bema@mail.mg',   '$2a$10$clientpasshash', TRUE),
    ('ANDRIAMANDR','Tantely',    'Fianarantsoa Nord',        '034 02 204 06', 'tantely@mail.mg',          '$2a$10$clientpasshash', FALSE),
    ('RAFIDY',     'Mampionona', 'Analakely, Tana',          '033 42 504 66', 'mampionona@mail.mg',       '$2a$10$clientpasshash', TRUE);

-- =====================================================
-- 6. VÉHICULES
-- =====================================================
INSERT INTO Vehicule (matricule, marque, modele, annee, capacite, statut) VALUES
    ('IMM-0001-T', 'Toyota',   'Hilux',    2020, 1500.00, 'Disponible'),
    ('IMM-0002-T', 'Renault',  'Kangoo',   2019,  800.00, 'Disponible'),
    ('IMM-0003-T', 'Mitsubishi','L200',    2021, 2000.00, 'En_course'),
    ('IMM-0004-T', 'Nissan',   'NV200',    2018,  700.00, 'En_panne');

-- =====================================================
-- 7. COMMANDES (variées sur les 30 derniers jours)
-- =====================================================
INSERT INTO Commandes (idclient, datecommande, mode_reception, idptdevente_retrait, adresse_livraison, plage_horaire_souhaitee, statutCommande, frais_livraison, total_produits, total_general) VALUES
    -- Commandes payées (pour les stats d'évolution)
    (1, NOW() - INTERVAL '28 days', 'Retrait_Boutique',   'BCA001', NULL,                       NULL,          'Paye',    0.00,   25000.00,  25000.00),
    (2, NOW() - INTERVAL '25 days', 'Livraison_Domicile', NULL,     'Ambohipo, Tana',            '08h-12h',     'Paye',  3000.00,  60000.00,  63000.00),
    (3, NOW() - INTERVAL '22 days', 'Retrait_Boutique',   'BTM003', NULL,                       NULL,          'Paye',    0.00,   35000.00,  35000.00),
    (4, NOW() - INTERVAL '20 days', 'Livraison_Domicile', NULL,     'Ankadifotsy, Tana',         '14h-18h',     'Paye',  3000.00,  85000.00,  88000.00),
    (5, NOW() - INTERVAL '18 days', 'Retrait_Boutique',   'BCA001', NULL,                       NULL,          'Paye',    0.00,   12500.00,  12500.00),
    (1, NOW() - INTERVAL '15 days', 'Livraison_Domicile', NULL,     'Itaosy, Tana',              '08h-12h',     'Paye',  3000.00,  45000.00,  48000.00),
    (6, NOW() - INTERVAL '12 days', 'Retrait_Boutique',   'BTM003', NULL,                       NULL,          'Paye',    0.00,   70000.00,  70000.00),
    (2, NOW() - INTERVAL '10 days', 'Livraison_Domicile', NULL,     'Ambohipo, Tana',            '14h-18h',     'Paye',  3000.00,  30000.00,  33000.00),
    (3, NOW() - INTERVAL '8 days',  'Retrait_Boutique',   'BCA001', NULL,                       NULL,          'Paye',    0.00,   20000.00,  20000.00),
    (8, NOW() - INTERVAL '6 days',  'Livraison_Domicile', NULL,     'Analakely, Tana',           '08h-12h',     'Paye',  3000.00,  85000.00,  88000.00),
    (4, NOW() - INTERVAL '5 days',  'Retrait_Boutique',   'BNT002', NULL,                       NULL,          'Paye',    0.00,   60000.00,  60000.00),
    (1, NOW() - INTERVAL '4 days',  'Livraison_Domicile', NULL,     'Itaosy, Tana',              '08h-12h',     'Paye',  3000.00,  35000.00,  38000.00),
    (5, NOW() - INTERVAL '3 days',  'Retrait_Boutique',   'BCA001', NULL,                       NULL,          'Paye',    0.00,   90000.00,  90000.00),
    (2, NOW() - INTERVAL '2 days',  'Livraison_Domicile', NULL,     'Ambohipo, Tana',            '14h-18h',     'Paye',  3000.00,  15000.00,  18000.00),
    (8, NOW() - INTERVAL '1 day',   'Retrait_Boutique',   'BCA001', NULL,                       NULL,          'Paye',    0.00,  100000.00, 100000.00),
    -- Commandes en cours / créées
    (7, NOW(),                       'Livraison_Domicile', NULL,     'Fianarantsoa Nord',         '08h-12h',     'En_cours',5000.00, 45000.00,  50000.00),
    (6, NOW() - INTERVAL '1 day',   'Retrait_Boutique',   'BTM003', NULL,                       NULL,          'Cree',    0.00,   20000.00,  20000.00),
    -- Commande annulée
    (3, NOW() - INTERVAL '14 days', 'Livraison_Domicile', NULL,     'Anosibe, Tana',             '14h-18h',     'Annule',3000.00,  35000.00,  38000.00);

-- =====================================================
-- 8. DÉTAILS COMMANDES
-- =====================================================
-- Commande 1: Compost Premium 5kg x2
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (1, 1, 2, 12500.00);
-- Commande 2: Pellets Chauffage + Engrais Liquide
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (2, 5, 1, 35000.00), (2, 3, 3, 8500.00);
-- Commande 3: Pellets Industriels
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (3, 6, 1, 35000.00);
-- Commande 4: Bouteille Biogaz 10L
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (4, 8, 1, 85000.00);
-- Commande 5: Compost Premium 5kg
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (5, 1, 1, 12500.00);
-- Commande 6: Bouteille Biogaz 5L
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (6, 7, 1, 45000.00);
-- Commande 7: Fromage x3 + Engrais granulé
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (7, 9, 3, 9000.00), (7, 4, 1, 15000.00), (7, 10, 3, 7500.00);
-- Commande 8: Compost Standard
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (8, 2, 1, 20000.00), (8, 10, 1, 7500.00);
-- Commande 9: Compost Standard 10kg
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (9, 2, 1, 20000.00);
-- Commande 10: Bouteille Biogaz 10L
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (10, 8, 1, 85000.00);
-- Commande 11: Pellets Industriels
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (11, 6, 1, 60000.00);
-- Commande 12: Pellets Chauffage
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (12, 5, 1, 35000.00);
-- Commande 13: Biogaz 10L + Pellets
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (13, 8, 1, 85000.00), (13, 5, 1, 35000.00);
-- Commande 14: Compost Premium
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (14, 1, 1, 12500.00), (14, 10, 1, 7500.00);
-- Commande 15: Biogaz 10L + Pellets Industriels
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (15, 8, 1, 85000.00), (15, 6, 1, 60000.00);
-- Commande 16 (en cours)
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (16, 7, 1, 45000.00);
-- Commande 17 (créée)
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (17, 1, 1, 20000.00);
-- Commande 18 (annulée)
INSERT INTO DetailsCommande (idcommande, idproduit, quantite, pu_au_moment_achat) VALUES (18, 5, 1, 35000.00);

-- =====================================================
-- 9. PAIEMENTS
-- =====================================================
INSERT INTO Paiement (idcommande, statut, date) VALUES
    (1,  'Cloture', CURRENT_DATE - 28),
    (2,  'Cloture', CURRENT_DATE - 25),
    (3,  'Cloture', CURRENT_DATE - 22),
    (4,  'Cloture', CURRENT_DATE - 20),
    (5,  'Cloture', CURRENT_DATE - 18),
    (6,  'Cloture', CURRENT_DATE - 15),
    (7,  'Cloture', CURRENT_DATE - 12),
    (8,  'Cloture', CURRENT_DATE - 10),
    (9,  'Cloture', CURRENT_DATE - 8),
    (10, 'Cloture', CURRENT_DATE - 6),
    (11, 'Cloture', CURRENT_DATE - 5),
    (12, 'Cloture', CURRENT_DATE - 4),
    (13, 'Cloture', CURRENT_DATE - 3),
    (14, 'Cloture', CURRENT_DATE - 2),
    (15, 'Cloture', CURRENT_DATE - 1),
    (16, 'Cree',    CURRENT_DATE),
    (17, 'Cree',    CURRENT_DATE);

-- =====================================================
-- 10. PAIEMENTS FILLE
-- =====================================================
INSERT INTO PaiementFille (idPaiement, typePayement, valeur) VALUES
    (1,  'Espece',       25000.00),
    (2,  'Mobile_Money', 63000.00),
    (3,  'Espece',       35000.00),
    (4,  'Mobile_Money', 88000.00),
    (5,  'Espece',       12500.00),
    (6,  'Carte_Fidelite',48000.00),
    (7,  'Espece',       35000.00),
    (7,  'Mobile_Money', 35000.00),
    (8,  'Mobile_Money', 33000.00),
    (9,  'Espece',       20000.00),
    (10, 'Mobile_Money', 88000.00),
    (11, 'Espece',       60000.00),
    (12, 'Mobile_Money', 38000.00),
    (13, 'Carte_Fidelite',90000.00),
    (14, 'Espece',       18000.00),
    (15, 'Mobile_Money',100000.00);

-- =====================================================
-- 11. LIVRAISONS
-- =====================================================
INSERT INTO Livraison (idvehicule, idlivreur, dateLivraison, statutLivraison, date) VALUES
    (1, 4, NOW() - INTERVAL '24 days', 'Livre',      CURRENT_DATE - 25),
    (2, 8, NOW() - INTERVAL '19 days', 'Livre',      CURRENT_DATE - 20),
    (1, 4, NOW() - INTERVAL '14 days', 'Livre',      CURRENT_DATE - 15),
    (3, 8, NOW() - INTERVAL '9 days',  'Livre',      CURRENT_DATE - 10),
    (2, 4, NOW() - INTERVAL '4 days',  'Livre',      CURRENT_DATE - 5),
    (1, 8, NOW() + INTERVAL '1 day',   'En_cours',   CURRENT_DATE);

INSERT INTO LivraisonFille (idLivraison, idCommande, statutLivraisonFille) VALUES
    (1, 2,  'Livre'),
    (2, 4,  'Livre'),
    (3, 6,  'Livre'),
    (4, 8,  'Livre'),
    (5, 10, 'Livre'),
    (6, 16, 'En_cours');

-- =====================================================
-- 12. TRANSFERTS INTER-BOUTIQUES
-- =====================================================
INSERT INTO Transferts (idPointDeVente, idPointDeVenteCible, statut_transfert, date_transfert) VALUES
    ('BCA001', 'BTM003', 'Termine',  NOW() - INTERVAL '20 days'),
    ('BCA001', 'BNT002', 'Termine',  NOW() - INTERVAL '10 days'),
    ('BNT002', 'BFR004', 'En_cours', NOW() - INTERVAL '2 days'),
    ('BCA001', 'BFR004', 'Cree',     NOW());

INSERT INTO TransfertsFille (idTransfert, idproduit, quantite) VALUES
    (1, 1, 20), (1, 2, 10), (1, 5, 5),
    (2, 3, 15), (2, 4, 10),
    (3, 6,  8), (3, 7,  5),
    (4, 9, 12), (4, 10, 8);

-- =====================================================
-- 13. MOUVEMENTS DE STOCK
-- =====================================================
INSERT INTO MvtStock (type_mouvement, idptdevente, dateMvt) VALUES
    ('Entree_Production', 'BCA001', NOW() - INTERVAL '30 days'),
    ('Sortie_Transfert',  'BCA001', NOW() - INTERVAL '20 days'),
    ('Entree_Boutique',   'BTM003', NOW() - INTERVAL '19 days'),
    ('Vente_Client',      'BCA001', NOW() - INTERVAL '15 days'),
    ('Vente_Client',      'BTM003', NOW() - INTERVAL '10 days'),
    ('Entree_Production', 'BCA001', NOW() - INTERVAL '5 days'),
    ('Perte',             'BNT002', NOW() - INTERVAL '3 days');

INSERT INTO MvtStockFille (idMvtStock, idproduit, quantite) VALUES
    (1, 1, 100), (1, 2, 80), (1, 3, 60), (1, 4, 50), (1, 5, 40),
    (2, 1, 20),  (2, 5, 5),
    (3, 1, 20),  (3, 5, 5),
    (4, 1, 5),   (4, 9, 10),
    (5, 6, 3),   (5, 7, 2),
    (6, 8, 30),  (6, 9, 50), (6, 10, 40),
    (7, 3, 2);

-- =====================================================
-- 14. TRÉSORERIE
-- =====================================================
INSERT INTO Tresorerie (type_mouvement, montant, date_operation, description, idcommande) VALUES
    ('Entree_Vente',       25000.00, NOW() - INTERVAL '28 days', 'Paiement commande #1', 1),
    ('Entree_Vente',       63000.00, NOW() - INTERVAL '25 days', 'Paiement commande #2', 2),
    ('Depense_Exploitation',15000.00,NOW() - INTERVAL '24 days', 'Carburant livraison',  NULL),
    ('Entree_Vente',       35000.00, NOW() - INTERVAL '22 days', 'Paiement commande #3', 3),
    ('Entree_Vente',       88000.00, NOW() - INTERVAL '20 days', 'Paiement commande #4', 4),
    ('Depense_Exploitation',8000.00, NOW() - INTERVAL '18 days', 'Entretien véhicule',   NULL),
    ('Entree_Vente',       12500.00, NOW() - INTERVAL '18 days', 'Paiement commande #5', 5),
    ('Entree_Vente',       48000.00, NOW() - INTERVAL '15 days', 'Paiement commande #6', 6),
    ('Entree_Vente',       70000.00, NOW() - INTERVAL '12 days', 'Paiement commande #7', 7),
    ('Depense_Exploitation',25000.00,NOW() - INTERVAL '10 days', 'Achat emballages',     NULL),
    ('Entree_Vente',       33000.00, NOW() - INTERVAL '10 days', 'Paiement commande #8', 8),
    ('Entree_Vente',       20000.00, NOW() - INTERVAL '8 days',  'Paiement commande #9', 9),
    ('Entree_Vente',       88000.00, NOW() - INTERVAL '6 days',  'Paiement commande #10',10),
    ('Entree_Vente',       60000.00, NOW() - INTERVAL '5 days',  'Paiement commande #11',11),
    ('Depense_Exploitation',12000.00,NOW() - INTERVAL '4 days',  'Facture électricité',  NULL),
    ('Entree_Vente',       38000.00, NOW() - INTERVAL '4 days',  'Paiement commande #12',12),
    ('Entree_Vente',       90000.00, NOW() - INTERVAL '3 days',  'Paiement commande #13',13),
    ('Entree_Vente',       18000.00, NOW() - INTERVAL '2 days',  'Paiement commande #14',14),
    ('Entree_Vente',      100000.00, NOW() - INTERVAL '1 day',   'Paiement commande #15',15);

-- =====================================================
-- 15. DEMANDES DE STOCK
-- =====================================================
INSERT INTO DemandeStock (idptdevente, dateDemande, dateCible) VALUES
    ('BTM003', NOW() - INTERVAL '15 days', NOW() - INTERVAL '10 days'),
    ('BFR004', NOW() - INTERVAL '8 days',  NOW() - INTERVAL '3 days'),
    ('BNT002', NOW() - INTERVAL '3 days',  NOW() + INTERVAL '2 days');

INSERT INTO DemandeStockFille (idDemandeStock, idproduit, quantite) VALUES
    (1, 1, 30), (1, 2, 20), (1, 5, 10),
    (2, 3, 15), (2, 9, 20),
    (3, 7, 10), (3, 8,  5);

-- =====================================================
-- 16. NOTIFICATIONS
-- =====================================================
INSERT INTO Notifications (typeMessage, objet, message, idptdevente, idDemandeStock, envoyeur) VALUES
    ('DemandeStock', 'Demande de réapprovisionnement', 'La boutique Toamasina a besoin de stock urgement.',   'BTM003', 1, TRUE),
    ('DemandeStock', 'Demande de stock Fianarantsoa',  'Besoin de produits bio pour la boutique Fianar.',    'BFR004', 2, TRUE),
    ('Autre',        'Maintenance système',            'Maintenance prévue le dimanche de 02h à 05h.',        'BCA001', NULL, TRUE),
    ('DemandeStock', 'Stock Nord Tana',                'Réapprovisionnement biogaz boutique Nord.',           'BNT002', 3, TRUE);

-- =====================================================
-- VÉRIFICATION RAPIDE
-- =====================================================
-- SELECT COUNT(*) AS nb_commandes_payees FROM Commandes WHERE statutCommande = 'Paye';
-- SELECT COUNT(*) AS nb_clients FROM Client;
-- SELECT COUNT(*) AS nb_produits FROM Produit;
