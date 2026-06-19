-- =========================================
-- 1. TABLE validation_mail AVEC STATUT
-- =========================================
CREATE TABLE validation_mail (
    id SERIAL PRIMARY KEY,
    id_client INT REFERENCES client (id) ON DELETE CASCADE,
    token VARCHAR(5),
    est_verifie BOOLEAN DEFAULT FALSE,
    date_expiration TIMESTAMP NOT NULL
);

-- =========================================
-- 2. FUNCTION GENERER OTP 5 CHIFFRES
-- =========================================
CREATE OR REPLACE FUNCTION generate_otp()
RETURNS TEXT AS $$
BEGIN
    RETURN LPAD(FLOOR(RANDOM() * 100000)::INT::TEXT, 5, '0');
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- 3. TRIGGER FUNCTION
-- =========================================
CREATE OR REPLACE FUNCTION set_validation_token()
RETURNS TRIGGER AS $$
BEGIN
    NEW.token := generate_otp();
    NEW.est_verifie := FALSE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- 4. TRIGGER
-- =========================================
CREATE TRIGGER trg_validation_token
BEFORE INSERT ON validation_mail
FOR EACH ROW
EXECUTE FUNCTION set_validation_token();

-- =========================================
-- 5. INDEX (BONNE PRATIQUE)
-- =========================================
CREATE INDEX idx_validation_mail_client ON validation_mail (id_client);

-- =========================================
-- 6. CONTRAINTE UNIQUE (OPTIONNEL MAIS PRO)
-- =========================================
ALTER TABLE validation_mail
ADD CONSTRAINT unique_token UNIQUE (token);