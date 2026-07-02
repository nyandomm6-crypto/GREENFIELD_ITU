INSERT INTO Employes (
    nom, prenom, adresse, contact, mail, motdepasse, role, idptdevente, est_actif, date
) VALUES
-- 1. Un Administrateur général rattaché à la Centrale
(
    'RAZAFIMAHATRATRA', 'Grace', 
    'IAH 23I Vontovorona', '034 55 123 45', 
    'grace.admin@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'ADMIN', 'CTR-001', TRUE, '2026-01-10'
),

-- 2. Un Responsable pour le Kiosque Nord
(
    'RABEMANANJARA', 'Sitraka', 
    'Analamahitsy, Antananarivo', '034 66 789 01', 
    'sitraka.rabe@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'RESPONSABLE', 'BTQ-001', TRUE, '2026-02-15'
),

-- 3. Un Vendeur pour le Kiosque Sud
(
    'RASOLO', 'Faly', 
    'Ankatso, Antananarivo', '033 77 888 99', 
    'faly.rasolo@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'VENDEUR', 'BTQ-002', TRUE, '2026-03-01'
),

-- 4. Un Vendeur pour le Kiosque Est
(
    'ANDRIANTOAVINA', 'Rova', 
    'Ambohipo, Antananarivo', '032 88 999 00', 
    'rova.andria@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'VENDEUR', 'BTQ-003', TRUE, '2026-04-20'
),

-- 5. Un ancien employé (Inactif) qui était au Kiosque Ouest
(
    'RAKOTOMALALA', 'Jean', 
    '67 Ha, Antananarivo', '034 00 111 22', 
    'jean.rakoto@greenfield.mg', 
    '$2a$10$vI8YV6A3p7yV0z4S6zX5eu2v1Vp4E2K/1Ew9fQ0wR3KzX5eu2v1Vp', -- "password"
    'VENDEUR', 'BTQ-004', FALSE, '2025-11-12'
);