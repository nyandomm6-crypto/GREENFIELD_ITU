CREATE TABLE IF NOT EXISTS faq (
    id SERIAL PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    reponse TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INTEGER NOT NULL DEFAULT 0
);

INSERT INTO
    faq (
        question,
        reponse,
        active,
        ordre
    )
VALUES (
        'Comment se passe la livraison ?',
        'Nous livrons dans toute l''île en 24 à 48 heures.',
        true,
        1
    ),
    (
        'Quels moyens de paiement acceptez-vous ?',
        'MVola, Orange Money, Airtel Money et carte bancaire.',
        true,
        2
    );