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