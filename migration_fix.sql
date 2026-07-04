-- ============================================================
-- GreenField migration fix
-- Created: 2026-07-01 — fixes Hibernate schema validation errors
-- ============================================================

-- 1. Create missing table: provincelivraison
CREATE TABLE IF NOT EXISTS provincelivraison (
    id   SERIAL PRIMARY KEY,
    nom  VARCHAR(100) NOT NULL
);

-- 2. Create missing table: statutcommande
CREATE TABLE IF NOT EXISTS statutcommande (
    id   SERIAL PRIMARY KEY,
    nom  VARCHAR(100)
);

-- 3. Create missing table: histstatutcommande
CREATE TABLE IF NOT EXISTS histstatutcommande (
    id               SERIAL PRIMARY KEY,
    idcommande       INTEGER REFERENCES commandes(id) ON DELETE CASCADE,
    idstatut         INTEGER REFERENCES statutcommande(id) ON DELETE SET NULL,
    datechangement   TIMESTAMP
);

-- 4. Create missing table: fraislivraison
CREATE TABLE IF NOT EXISTS fraislivraison (
    id              SERIAL PRIMARY KEY,
    idprovince      INTEGER REFERENCES provincelivraison(id) ON DELETE SET NULL,
    poidsreference  NUMERIC(10,2) NOT NULL,
    montant         NUMERIC(10,2) NOT NULL
);

-- 5. Add missing FK column on commandes: provincelivraisonid
ALTER TABLE commandes
    ADD COLUMN IF NOT EXISTS provincelivraisonid INTEGER REFERENCES provincelivraison(id) ON DELETE SET NULL;

-- 6. Add missing FK column on commandes: statutactuel
ALTER TABLE commandes
    ADD COLUMN IF NOT EXISTS statutactuel INTEGER REFERENCES statutcommande(id) ON DELETE SET NULL;
