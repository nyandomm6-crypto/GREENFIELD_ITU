INSERT INTO PointDeVente (nom, code, adresse, contact) VALUES
-- 1. Centrale principale
('Centrale Principale', 'CTR-001', 'Zone Industrielle Antanimena', '034 22 333 44'),

-- 2. Kiosque Nord
('Kiosque Nord', 'BTQ-001', 'Analamahitsy, Antananarivo', '034 66 789 01'),

-- 3. Kiosque Sud
('Kiosque Sud', 'BTQ-002', 'Ankatso, Antananarivo', '033 77 888 99'),

-- 4. Kiosque Est
('Kiosque Est', 'BTQ-003', 'Ambohipo, Antananarivo', '032 88 999 00'),

-- 5. Kiosque Ouest
('Kiosque Ouest', 'BTQ-004', '67 Ha, Antananarivo', '034 00 111 22');

INSERT INTO Employes (
    nom, prenom, adresse, contact, mail, motdepasse, role, idptdevente, est_actif, date
) VALUES
-- 1. Un Administrateur général rattaché à la Centrale
(
    'RAZAFIMAHATRATRA', 'Grace', 
    'IAH 23I Vontovorona', '034 55 123 45', 
    'grace.admin@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Administrateur', 'CTR-001', TRUE, '2026-01-10'
),

-- 2. Un Manager pour le Kiosque Nord
(
    'RABEMANANJARA', 'Sitraka', 
    'Analamahitsy, Antananarivo', '034 66 789 01', 
    'sitraka.rabe@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Manager', 'BTQ-001', TRUE, '2026-02-15'
),

-- 3. Un Caissier au Kiosque Sud
(
    'RASOLO', 'Faly', 
    'Ankatso, Antananarivo', '033 77 888 99', 
    'faly.rasolo@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Caissier', 'BTQ-002', TRUE, '2026-03-01'
),

-- 4. Un Livreur rattaché au Kiosque Est
(
    'ANDRIANTOAVINA', 'Rova', 
    'Ambohipo, Antananarivo', '032 88 999 00', 
    'rova.andria@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Livreur', 'BTQ-003', TRUE, '2026-04-20'
),

-- 5. Un Employé standard au Kiosque Ouest
(
    'RAKOTOMALALA', 'Jean', 
    '67 Ha, Antananarivo', '034 00 111 22', 
    'jean.rakoto@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'Employe', 'BTQ-004', TRUE, '2026-05-12'
);

-- Catégories de produits
INSERT INTO CategorieProduit (libelle) VALUES
('Légumes'),
('Fruits'),
('Produits laitiers');

-- Produits
INSERT INTO Produit (nom, matricule, pu, idcategorie) VALUES
('Tomate', 'PRD-001', 2000, 1),
('Carotte', 'PRD-002', 1500, 1),
('Orange', 'PRD-003', 3000, 2),
('Pomme', 'PRD-004', 2500, 2),
('Lait', 'PRD-005', 4000, 3);

-- Mouvements de stock pour la Centrale (CTR-001)
INSERT INTO MvtStock (type_mouvement, idptdevente, dateMvt) VALUES
('Entree_Production', 'CTR-001', '2026-01-15 10:00:00'),
('Sortie_Transfert', 'CTR-001', '2026-01-16 14:30:00'),
('Entree_Production', 'CTR-001', '2026-01-17 09:00:00'),
('Perte', 'CTR-001', '2026-01-18 16:00:00');

-- Mouvements fille pour la Centrale
INSERT INTO MvtStockFille (idMvtStock, idproduit, quantite) VALUES
(1, 1, 50),  -- Tomate - Entrée_Production
(1, 2, 30),  -- Carotte - Entrée_Production
(2, 1, 20),  -- Tomate - Sortie_Transfert
(3, 3, 40),  -- Orange - Entrée_Production
(3, 4, 25),  -- Pomme - Entrée_Production
(4, 2, 5);   -- Carotte - Perte

-- Mouvements de stock pour les Boutiques
INSERT INTO MvtStock (type_mouvement, idptdevente, dateMvt) VALUES
('Entree_Boutique', 'BTQ-001', '2026-01-20 10:00:00'),
('Vente_Client', 'BTQ-001', '2026-01-21 14:30:00'),
('Entree_Boutique', 'BTQ-001', '2026-01-22 09:00:00'),
('Perte', 'BTQ-001', '2026-01-23 16:00:00'),
('Entree_Boutique', 'BTQ-002', '2026-01-24 10:00:00'),
('Vente_Client', 'BTQ-002', '2026-01-25 14:30:00');

-- Mouvements fille pour les Boutiques
INSERT INTO MvtStockFille (idMvtStock, idproduit, quantite) VALUES
(5, 1, 15),  -- Tomate - Entree_Boutique BTQ-001
(5, 3, 10),  -- Orange - Entree_Boutique BTQ-001
(6, 1, 5),   -- Tomate - Vente_Client BTQ-001
(6, 3, 3),   -- Orange - Vente_Client BTQ-001
(7, 4, 20),  -- Pomme - Entree_Boutique BTQ-001
(7, 5, 10),  -- Lait - Entree_Boutique BTQ-001
(8, 3, 2),   -- Orange - Perte BTQ-001
(9, 2, 25),  -- Carotte - Entree_Boutique BTQ-002
(9, 4, 15),  -- Pomme - Entree_Boutique BTQ-002
(10, 2, 8),  -- Carotte - Vente_Client BTQ-002
(10, 4, 5);  -- Pomme - Vente_Client BTQ-002