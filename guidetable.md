# Guide des Normes et Standards de la Base de Données

## 1. Conventions de Nommage
Afin de maintenir un code SQL lisible et uniforme pour l'ensemble de l'équipe, les règles suivantes s'appliquent :

* **Tables** : Les noms des tables s'écrivent en **PascalCase** (ex: `PointDeVente`, `DetailsCommande`). Ils sont au singulier, sauf exception pour les entités représentant structurellement un collectif (ex: `Employes`).
* **Colonnes** : Les attributs s'écrivent en **camelCase** (ex: `dateCommande`, `puAuMomentAchat`, `fraisLivraison`).
* **Clés Primaires** : Toute table doit posséder une clé primaire unique, auto-incrémentée et systématiquement nommée `id` (de type `SERIAL` ou `BIGSERIAL`).
* **Clés Étrangères** : Pour assurer une traçabilité directe, une clé étrangère doit être nommée en combinant le préfixe `id` suivi du nom de la table cible (ex: `idClient`).
* **Types Énumérés (ENUM)** : Les types personnalisés doivent être préfixés ou suffixés de manière claire (ex: `f_role`, `type_mvt`) et leurs valeurs s'écrivent en **PascalCase** ou **Snake_Case** (ex: `Mobile_Money`, `Administrateur`).

---

## 2. Types de Données et Précision Financière
* **Montants Monétaires (Ariary)** : Pour éviter les erreurs d'arrondi inhérentes aux types flottants (`FLOAT`, `REAL`), tous les prix, taxes, frais et totaux doivent impérativement utiliser le type `DECIMAL(10, 2)` (ou `NUMERIC`). Le stockage s'effectue en Ariary (Ar).
* **Dates et Heures** : Pour assurer la traçabilité des commandes, collectes et mouvements, le type `TIMESTAMP` (ou `TIMESTAMP WITH TIME ZONE`) est obligatoire. La valeur par défaut doit être initialisée via `DEFAULT CURRENT_TIMESTAMP`.
* **Chaînes de Caractères** : Les mots de passe hachés doivent disposer d'une allocation minimale de `VARCHAR(255)`. Les adresses électroniques uniques doivent utiliser au moins `VARCHAR(150)`.

---

## 3. Règles d'Intégrité Référentielle et Historisation
* **Sécurisation des Historiques d'Achats** : Dans la table `DetailsCommande`, le prix unitaire du produit ne doit jamais être récupéré par une simple jointure dynamique lors de la lecture. Il doit être écrit en dur dans la colonne `pu_au_moment_achat` au moment exact de la validation de la commande. Cela évite qu'une modification ultérieure du catalogue des produits n'altère rétroactivement les chiffres d'affaires passés.
* **Comportement à la Suppression (`ON DELETE`)** :
  * Les suppressions en cascade (`ON DELETE CASCADE`) sont réservées aux tables dépendantes strictes (ex: si une commande est supprimée, ses lignes dans `DetailsCommande` n'ont plus lieu d'exister).
  * Pour les entités pivots, le choix doit se porter sur `ON DELETE SET NULL` (ex: si un véhicule est supprimé, la livraison historique associée conserve sa trace avec une valeur `NULL` pour le véhicule) ou sur l'utilisation d'un drapeau d'activation (ex: `est_actif BOOLEAN DEFAULT TRUE` dans la table `Employes` pour gérer un archivage logique plutôt qu'une suppression physique).

---

## 4. Alignement avec les Règles Métiers du MVP
L'architecture physique des tables doit valider informatiquement les contraintes strictes du cahier des charges de la V1[cite: 1] :
* **Règle des Rôles** : Le champ `role` de la table `Employes` s'appuie sur une énumération stricte contenant exclusivement les 8 profils autorisés par la matrice des droits (Client, Administrateur, Caissier, Livreur, Employé, Responsable Financier, RH, Manager).
* **Règle de Fidélité (50 000 Ar / 10 cases)** : La table `CarteFideliteClient` est conçue pour stocker l'état d'avancement de la carte virtuelle. Elle contient un compteur de cases (allant de 0 à 10) et un indicateur booléen `est_valide` mis à jour dès le dixième achat admissible.
* **Logistique & Seuil de Gratuité** : La structure de la table `Commandes` sépare distinctement le montant des produits (`total_produits`) et les `frais_livraison`[cite: 2]. Cela permet à l'application d'injecter une valeur de `0.00` pour les frais si le total cumulé atteint ou dépasse les `200 000 Ar` requis.