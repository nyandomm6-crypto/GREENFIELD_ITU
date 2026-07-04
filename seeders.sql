INSERT INTO
    CategorieProduit (libelle)
VALUES ('Compost'),
    ('Terre'),
    ('Plante pour reboisement'),
    ('Plante potagère'),
    ('Plante fleurie');

INSERT INTO
    Produit (
        nom,
        matricule,
        pu,
        idcategorie
    )
VALUES (
        'Compost (1kg)',
        'CMP-001',
        3000.00,
        1
    ),
    (
        'Compost (5kg)',
        'CMP-002',
        13500.00,
        1
    ),
    (
        'Compost (10kg)',
        'CMP-003',
        27000.00,
        1
    ),
    (
        'Compost (50kg)',
        'CMP-004',
        121500.00,
        1
    ),
    (
        'Terre melangée',
        'ENG-001',
        8000.00,
        2
    ),
    ('Pin', 'PLTR-001', 1700.00, 3),
    (
        'Ficus',
        'PLTR-002',
        1700.00,
        3
    ),
    (
        'Monstera',
        'PLTR-003',
        1700.00,
        3
    ),
    (
        'Succulente',
        'PLTR-004',
        1700.00,
        3
    ),
    (
        'Bambou',
        'PLTR-005',
        1700.00,
        3
    ),
    (
        'Orangier',
        'PLTP-001',
        2950.00,
        4
    ),
    (
        'Tomate',
        'PLTP-002',
        2950.00,
        4
    ),
    (
        'Basilic',
        'PLTP-003',
        2950.00,
        4
    ),
    (
        'Menthe',
        'PLTP-004',
        2950.00,
        4
    ),
    (
        'Persil',
        'PLTP-005',
        2950.00,
        4
    ),
    (
        'Rose',
        'PLTF-001',
        15000.00,
        5
    ),
    (
        'Tulipe',
        'PLTF-002',
        35000.00,
        5
    ),
    (
        'Lys',
        'PLTF-003',
        25000.00,
        5
    ),
    (
        'Orchidée',
        'PLTF-004',
        12000.00,
        5
    ),
    (
        'Géranium',
        'PLTF-005',
        18000.00,
        5
    );

-- Categorie 1 : Compost (CMP)
UPDATE Produit SET poids_moyenne_unitaire = 1.0 WHERE id = 1;
-- Compost (1kg)
UPDATE Produit SET poids_moyenne_unitaire = 5.0 WHERE id = 2;
-- Compost (5kg)
UPDATE Produit SET poids_moyenne_unitaire = 10.0 WHERE id = 3;
-- Compost (10kg)
UPDATE Produit SET poids_moyenne_unitaire = 50.0 WHERE id = 4;
-- Compost (50kg)
-- Categorie 2 : Terre (ENG)
UPDATE Produit SET poids_moyenne_unitaire = 15.0 WHERE id = 5;
-- Terre melangée
-- Categorie 3 : Plantes de Terre / Décoratives (PLTR)
UPDATE Produit SET poids_moyenne_unitaire = 2.5 WHERE id = 6;
-- Pin (jeune plant en pot)
UPDATE Produit SET poids_moyenne_unitaire = 1.2 WHERE id = 7;
-- Ficus (taille moyenne de bureau)
UPDATE Produit SET poids_moyenne_unitaire = 1.8 WHERE id = 8;
-- Monstera (feuillage et pot moyen)
UPDATE Produit SET poids_moyenne_unitaire = 0.3 WHERE id = 9;
-- Succulente (très léger, petit godet)
UPDATE Produit SET poids_moyenne_unitaire = 2.0 WHERE id = 10;
-- Bambou (motte plus dense)

-- Categorie 4 : Plantes Potagères / Aromatiques (PLTP)
UPDATE Produit SET poids_moyenne_unitaire = 1.5 WHERE id = 11;
-- Orangier (jeune arbuste greffé)
UPDATE Produit SET poids_moyenne_unitaire = 0.4 WHERE id = 12;
-- Tomate (jeune pousse en godet)
UPDATE Produit SET poids_moyenne_unitaire = 0.2 WHERE id = 13;
-- Basilic (petit pot aromatique)
UPDATE Produit SET poids_moyenne_unitaire = 0.2 WHERE id = 14;
-- Menthe (petit pot aromatique)
UPDATE Produit SET poids_moyenne_unitaire = 0.2 WHERE id = 15;
-- Persil (petit pot aromatique)

-- Categorie 5 : Plantes à Fleurs / Ornementales (PLTF)
UPDATE Produit SET poids_moyenne_unitaire = 1.0 WHERE id = 16;
-- Rose (rosier en pot de culture)
UPDATE Produit SET poids_moyenne_unitaire = 0.5 WHERE id = 17;
-- Tulipe (pot de bulbes)
UPDATE Produit SET poids_moyenne_unitaire = 0.8 WHERE id = 18;
-- Lys (pot moyen)
UPDATE Produit SET poids_moyenne_unitaire = 0.6 WHERE id = 19;
-- Orchidée (substrat d'écorce léger)
UPDATE Produit SET poids_moyenne_unitaire = 0.7 WHERE id = 20;
-- Géranium (pot de balconnière classique)

INSERT INTO
    pointdevente (nom, code, adresse, contact)
VALUES (
        'Centrale',
        'CTR-001',
        'IAH 23I Vontovorona',
        '034 12 345 67'
    ),
    (
        'Kiosque Nord',
        'BTQ-001',
        'Avenue du Nord, Antananarivo',
        '034 12 345 68'
    ),
    (
        'Kiosque Sud',
        'BTQ-002',
        'Avenue du Sud, Antananarivo',
        '034 12 345 69'
    ),
    (
        'Kiosque Est',
        'BTQ-003',
        'Avenue Est, Antananarivo',
        '034 12 345 70'
    ),
    (
        'Kiosque Ouest',
        'BTQ-004',
        'Avenue Ouest, Antananarivo',
        '034 12 345 71'
    );

INSERT INTO
    statutcommande (nom)
VALUES ('Créée'),
    ('Payée'),
    ('Livrée'),
    ('Anulée');

INSERT INTO
    provinceLivraison (nom)
VALUES ('Antananarivo'),
    ('Toamasina'),
    ('Antsiranana'),
    ('Fianarantsoa'),
    ('Mahajanga'),
    ('Toliara');

INSERT INTO
    fraisLivraison (
        idprovince,
        poidsreference,
        montant
    )
VALUES (1, 0.0, 5000.00), -- Antananarivo, supérieur à 0kg
    (1, 5.0, 10000.00), -- Antananarivo, supérieur à 5kg
    (1, 10.0, 15000.00), -- Antananarivo, supérieur à 10kg
    (1, 20.0, 25000.00), -- Antananarivo, supérieur à 20kg
    (1, 50.0, 40000.00), -- Antananarivo, supérieur à 50kg
    (1, 100.0, 60000.00), -- Antananarivo, supérieur à 100kg
    (1, 200.0, 100000.00), -- Antananarivo, supérieur à 200kg
    (1, 500.0, 200000.00), -- Antananarivo, supérieur à 500kg
    (1, 1000.0, 350000.00), -- Antananarivo, supérieur à 1000kg
    (1, 1500.0, 500000.00), -- Antananarivo, supérieur à 1500kg
    (1, 2000.0, 700000.00), -- Antananarivo, supérieur à 2000kg
    (1, 5000.0, 1500000.00), -- Antananarivo, supérieur à 5000kg
    (2, 0.0, 10000.00), -- Toamasina, supérieur à 0kg
    (2, 5.0, 15000.00), -- Toamasina, supérieur à 5kg
    (2, 10.0, 25000.00), -- Toamasina, supérieur à 10kg
    (2, 20.0, 40000.00), -- Toamasina, supérieur à 20kg
    (2, 50.0, 70000.00), -- Toamasina, supérieur à 50kg
    (2, 100.0, 100000.00), -- Toamasina, supérieur à 100kg
    (2, 200.0, 150000.00), -- Toamasina, supérieur à 200kg
    (2, 500.0, 300000.00), -- Toamasina, supérieur à 500kg
    (2, 1000.0, 500000.00), -- Toamasina, supérieur à 1000kg
    (2, 1500.0, 800000.00), -- Toamasina, supérieur à 1500kg
    (2, 2000.0, 1200000.00), -- Toamasina, supérieur à 2000kg
    (2, 5000.0, 2500000.00), -- Toamasina, supérieur à 5000kg
    (3, 0.0, 15000.00), -- Antsiranana, supérieur à 0kg
    (3, 5.0, 20000.00), -- Antsiranana, supérieur à 5kg
    (3, 10.0, 30000.00), -- Antsiranana, supérieur à 10kg
    (3, 20.0, 50000.00), -- Antsiranana, supérieur à 20kg
    (3, 50.0, 80000.00), -- Antsiranana, supérieur à 50kg
    (3, 100.0, 120000.00), -- Antsiranana, supérieur à 100kg
    (3, 200.0, 200000.00), -- Antsiranana, supérieur à 200kg
    (3, 500.0, 400000.00), -- Antsiranana, supérieur à 500kg
    (3, 1000.0, 700000.00), -- Antsiranana, supérieur à 1000kg
    (3, 1500.0, 1000000.00), -- Antsiranana, supérieur à 1500kg
    (3, 2000.0, 1400000.00), -- Antsiranana, supérieur à 2000kg
    (3, 5000.0, 3000000.00), -- Antsiranana, supérieur à 5000kg
    (4, 0.0, 12000.00), -- Fianarantsoa, supérieur à 0kg
    (4, 5.0, 15000.00), -- Fianarantsoa, supérieur à 5kg
    (4, 10.0, 25000.00), -- Fianarantsoa, supérieur à 10kg
    (4, 20.0, 40000.00), -- Fianarantsoa, supérieur à 20kg
    (4, 50.0, 70000.00), -- Fianarantsoa, supérieur à 50kg
    (4, 100.0, 100000.00), -- Fianarantsoa, supérieur à 100kg
    (4, 200.0, 150000.00), -- Fianarantsoa, supérieur à 200kg
    (4, 500.0, 300000.00), -- Fianarantsoa, supérieur à 500kg
    (4, 1000.0, 500000.00), -- Fianarantsoa, supérieur à 1000kg
    (4, 1500.0, 800000.00), -- Fianarantsoa, supérieur à 1500kg
    (4, 2000.0, 1200000.00), -- Fianarantsoa, supérieur à 2000kg
    (4, 5000.0, 2500000.00), -- Fianarantsoa, supérieur à 5000kg
    (5, 0.0, 15000.00), -- Mahajanga, supérieur à 0kg
    (5, 5.0, 18000.00), -- Mahajanga, supérieur à 5kg
    (5, 10.0, 28000.00), -- Mahajanga, supérieur à 10kg
    (5, 20.0, 45000.00), -- Mahajanga, supérieur à 20kg
    (5, 50.0, 75000.00), -- Mahajanga, supérieur à 50kg
    (5, 100.0, 110000.00), -- Mahajanga, supérieur à 100kg
    (5, 200.0, 180000.00), -- Mahajanga, supérieur à 200kg
    (5, 500.0, 350000.00), -- Mahajanga, supérieur à 500kg
    (5, 1000.0, 600000.00), -- Mahajanga, supérieur à 1000kg
    (5, 1500.0, 900000.00), -- Mahajanga, supérieur à 1500kg
    (5, 2000.0, 1300000.00), -- Mahajanga, supérieur à 2000kg
    (5, 5000.0, 2800000.00), -- Mahajanga, supérieur à 5000kg
    (6, 0.0, 20000.00), -- Toliara, supérieur à 0kg
    (6, 5.0, 22000.00), -- Toliara, supérieur à 5kg
    (6, 10.0, 35000.00), -- Toliara, supérieur à 10kg
    (6, 20.0, 60000.00), -- Toliara, supérieur à 20kg
    (6, 50.0, 100000.00), -- Toliara, supérieur à 50kg
    (6, 100.0, 150000.00), -- Toliara, supérieur à 100kg
    (6, 200.0, 250000.00), -- Toliara, supérieur à 200kg
    (6, 500.0, 500000.00), -- Toliara, supérieur à 500kg
    (6, 1000.0, 900000.00), -- Toliara, supérieur à 1000kg
    (6, 1500.0, 1300000.00), -- Toliara, supérieur à 1500kg
    (6, 2000.0, 1800000.00), -- Toliara, supérieur à 2000kg
    (6, 5000.0, 4000000.00);
-- Toliara, supérieur à 5000kg

INSERT INTO
    Employes (
        nom,
        prenom,
        adresse,
        contact,
        mail,
        motdepasse,
        role,
        idptdevente,
        est_actif,
        date
    )
VALUES
    -- 1. Un Administrateur général rattaché à la Centrale
    (
        'RAZAFIMAHATRATRA',
        'Grace',
        'IAH 23I Vontovorona',
        '034 55 123 45',
        'grace.admin@greenfield.mg',
        '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
        'Administrateur',
        'CTR-001',
        TRUE,
        '2026-01-10'
    ),

-- 2. Un Manager pour le Kiosque Nord
(
    'RABEMANANJARA',
    'Sitraka',
    'Analamahitsy, Antananarivo',
    '034 66 789 01',
    'sitraka.rabe@greenfield.mg',
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Manager',
    'BTQ-001',
    TRUE,
    '2026-02-15'
),

-- 3. Un Caissier au Kiosque Sud
(
    'RASOLO',
    'Faly',
    'Ankatso, Antananarivo',
    '033 77 888 99',
    'faly.rasolo@greenfield.mg',
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Caissier',
    'BTQ-002',
    TRUE,
    '2026-03-01'
),

-- 4. Un Livreur rattaché au Kiosque Est
(
    'ANDRIANTOAVINA',
    'Rova',
    'Ambohipo, Antananarivo',
    '032 88 999 00',
    'rova.andria@greenfield.mg',
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Livreur',
    'BTQ-003',
    TRUE,
    '2026-04-20'
),

-- 5. Un Employé standard au Kiosque Ouest
(
    'RAKOTOMALALA',
    'Jean',
    '67 Ha, Antananarivo',
    '034 00 111 22',
    'jean.rakoto@greenfield.mg',
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Employe',
    'BTQ-004',
    TRUE,
    '2026-05-12'
);