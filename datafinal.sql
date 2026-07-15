

-- 1. Catégories de produits
INSERT INTO CategorieProduit (libelle)
VALUES
    ('Compost'),
    ('Terre'),
    ('Plante pour reboisement'),
    ('Plante potagère'),
    ('Plante fleurie');

-- 2. Produits (poids intégré directement)
INSERT INTO Produit (nom, matricule, pu, idcategorie, poids_moyenne_unitaire)
VALUES
    ('Compost (5kg)',  'CMP-001', 3000.00,   1, 5.0),
    ('Compost (10kg)', 'CMP-002', 13500.00,  1, 10.0),
    ('Compost (20kg)', 'CMP-003', 27000.00,  1, 20.0),
    ('Compost (25kg)', 'CMP-004', 121500.00, 1, 25.0),

    ('Pin',        'PLTR-001', 1700.00, 3, 2.5),
    ('Ficus',      'PLTR-002', 1700.00, 3, 1.2),
    ('Monstera',   'PLTR-003', 1700.00, 3, 1.8),
    ('Succulente', 'PLTR-004', 1700.00, 3, 0.3),
    ('Bambou',     'PLTR-005', 1700.00, 3, 2.0),

    ('Orangier', 'PLTP-001', 2950.00, 4, 1.5),
    ('Tomate',   'PLTP-002', 2950.00, 4, 0.4),
    ('Basilic',  'PLTP-003', 2950.00, 4, 0.2),
    ('Menthe',   'PLTP-004', 2950.00, 4, 0.2),
    ('Persil',   'PLTP-005', 2950.00, 4, 0.2),

    ('Rose',       'PLTF-001', 15000.00, 5, 1.0),
    ('Tulipe',     'PLTF-002', 35000.00, 5, 0.5),
    ('Lys',        'PLTF-003', 25000.00, 5, 0.8),
    ('Orchidée',   'PLTF-004', 12000.00, 5, 0.6),
    ('Rose',       'PLTF-005', 10000.00, 5, 1.0),
    ('Marguerite', 'PLTF-006', 12000.00, 5, 0.5),
    ('Géranium',   'PLTF-007', 18000.00, 5, 0.7);

-- 3. Points de vente (les deux blocs d'origine fusionnés)
INSERT INTO PointDeVente (nom, code, adresse, contact)
VALUES
    ('Centrale',                  'CTR-001', 'IAH 23I Vontovorona',        '034 12 345 67'),
    ('Kiosque Nord',               'BTQ-001', 'Avenue du Nord, Antananarivo', '034 12 345 68'),
    ('Kiosque Sud',                'BTQ-002', 'Avenue du Sud, Antananarivo',  '034 12 345 69'),
    ('Kiosque Est',                'BTQ-003', 'Avenue Est, Antananarivo',     '034 12 345 70'),
    ('Kiosque Ouest',              'BTQ-004', 'Avenue Ouest, Antananarivo',   '034 12 345 71'),
    ('Point de vente Antananarivo','PT001',   'Antananarivo', '0340001001'),
    ('Point de vente Toamasina',   'PT002',   'Toamasina',    '0340001002'),
    ('Point de vente Fianarantsoa','PT003',   'Fianarantsoa', '0340001003');

-- 4. Statuts de commande
INSERT INTO StatutCommande (nom)
VALUES
    ('Créée'),
    ('Payée'),
    ('Livrée'),
    ('Annulée'),
    ('En cours de livraison');

-- 5. Provinces de livraison
INSERT INTO ProvinceLivraison (nom)
VALUES
    ('Antananarivo'),
    ('Toamasina'),
    ('Antsiranana'),
    ('Fianarantsoa'),
    ('Mahajanga'),
    ('Toliara');

-- 6. Frais de livraison
INSERT INTO FraisLivraison (idprovince, poidsreference, montant)
VALUES
    (1, 0.0,    5000.00),
    (1, 5.0,    10000.00),
    (1, 10.0,   15000.00),
    (1, 20.0,   25000.00),
    (1, 50.0,   40000.00),
    (1, 100.0,  60000.00),
    (1, 200.0,  100000.00),
    (1, 500.0,  200000.00),
    (1, 1000.0, 350000.00),
    (1, 1500.0, 500000.00),
    (1, 2000.0, 700000.00),
    (1, 5000.0, 1500000.00),

    (2, 0.0,    10000.00),
    (2, 5.0,    15000.00),
    (2, 10.0,   25000.00),
    (2, 20.0,   40000.00),
    (2, 50.0,   70000.00),
    (2, 100.0,  100000.00),
    (2, 200.0,  150000.00),
    (2, 500.0,  300000.00),
    (2, 1000.0, 500000.00),
    (2, 1500.0, 800000.00),
    (2, 2000.0, 1200000.00),
    (2, 5000.0, 2500000.00),

    (3, 0.0,    15000.00),
    (3, 5.0,    20000.00),
    (3, 10.0,   30000.00),
    (3, 20.0,   50000.00),
    (3, 50.0,   80000.00),
    (3, 100.0,  120000.00),
    (3, 200.0,  200000.00),
    (3, 500.0,  400000.00),
    (3, 1000.0, 700000.00),
    (3, 1500.0, 1000000.00),
    (3, 2000.0, 1400000.00),
    (3, 5000.0, 3000000.00),

    (4, 0.0,    12000.00),
    (4, 5.0,    15000.00),
    (4, 10.0,   25000.00),
    (4, 20.0,   40000.00),
    (4, 50.0,   70000.00),
    (4, 100.0,  100000.00),
    (4, 200.0,  150000.00),
    (4, 500.0,  300000.00),
    (4, 1000.0, 500000.00),
    (4, 1500.0, 800000.00),
    (4, 2000.0, 1200000.00),
    (4, 5000.0, 2500000.00),

    (5, 0.0,    15000.00),
    (5, 5.0,    18000.00),
    (5, 10.0,   28000.00),
    (5, 20.0,   45000.00),
    (5, 50.0,   75000.00),
    (5, 100.0,  110000.00),
    (5, 200.0,  180000.00),
    (5, 500.0,  350000.00),
    (5, 1000.0, 600000.00),
    (5, 1500.0, 900000.00),
    (5, 2000.0, 1300000.00),
    (5, 5000.0, 2800000.00),

    (6, 0.0,    20000.00),
    (6, 5.0,    22000.00),
    (6, 10.0,   35000.00),
    (6, 20.0,   60000.00),
    (6, 50.0,   100000.00),
    (6, 100.0,  150000.00),
    (6, 200.0,  250000.00),
    (6, 500.0,  500000.00),
    (6, 1000.0, 900000.00),
    (6, 1500.0, 1300000.00),
    (6, 2000.0, 1800000.00),
    (6, 5000.0, 4000000.00);

-- 7. Employés
INSERT INTO Employes (nom, prenom, adresse, contact, mail, motdepasse, role, idptdevente)
VALUES
    ('Rakoto',    'Admin', 'Antananarivo', '0340000001', 'admin@gmail.com',    'admin123',    'Administrateur', NULL),
    ('Rabe',      'Jean',  'Antananarivo', '0340000002', 'caissier@gmail.com', 'caissier123', 'Caissier',       'PT001'),
    ('Randria',   'Paul',  'Antananarivo', '0346678433', 'livreur@gmail.com',  'livreur123',  'Livreur',        'PT001'),
    ('Rasoanaivo','Marie', 'Antananarivo', '0340000004', 'employe@gmail.com',  'employe123',  'Employe',        'PT001');

-- 8. Véhicules
INSERT INTO Vehicule (matricule, marque, modele, annee, capacite, statut)
VALUES
    ('MAT001', 'Toyota',       'Hiace',    2020, 15.00, 'Disponible'),
    ('MAT002', 'Mercedes-Benz','Sprinter', 2021, 20.00, 'Disponible'),
    ('MAT003', 'Ford',         'Transit',  2019, 18.00, 'Disponible'),
    ('MAT004', 'Hyundai',      'H350',     2022, 16.50, 'Disponible'),
    ('MAT005', 'Renault',      'Master',   2023, 17.00, 'Disponible');