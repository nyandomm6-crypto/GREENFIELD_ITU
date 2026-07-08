# Module Statistiques

## 1. Fonctionnalites couvertes

- Tableau de bord : `/dashboard` et `/stats`
- Evolution des ventes : `/stats/ventes`
- Top produits : `/stats/produits`
- Top clients : `/stats/clients`
- Tresorerie : `/stats/tresorerie`

Le module front consomme les API suivantes :

- `GET /api/back/statistiques/produits`
- `GET /api/back/statistiques/evolution-ventes`
- `GET /api/back/statistiques/top-clients`
- `GET /api/back/statistiques/top-produits`
- `GET /api/back/statistiques/historique-ventes`
- `GET /api/back/statistiques/benefice-fromage`
- `POST /api/back/statistiques/tresorerie`

## 2. Regles de gestion

### 2.1 Tableau de bord statistique

- La page dashboard regroupe les entrees du module et sert de point d'acces aux autres vues.
- Chaque tuile redirige vers une page dediee : ventes, produits, clients ou tresorerie.
- Les statistiques affichees proviennent des API back du module statistique.

### 2.2 Evolution des ventes

- Les donnees sont filtrees par annee et par produit.
- Si aucun filtre n'est fourni, la consultation porte sur l'ensemble des donnees disponibles.
- Les resultats sont aggregates par date pour afficher l'evolution journaliere du chiffre d'affaires.

### 2.3 Top produits

- La page affiche le classement des produits les plus vendus sur la periode choisie.
- Le filtre annee limite la selection aux ventes de l'annee demandee.
- Le calcul est base sur la quantite vendue par produit.

### 2.4 Top clients

- La page affiche les 5 meilleurs clients en ligne selon le total depense.
- Le filtre annee limite le classement a la periode demandee.
- L'ordre d'affichage est decroissant sur le total depense.

### 2.5 Tresorerie

- La page affiche le chiffre d'affaires fromage, le total des entrees, le total des depenses et le benefice net.
- Les filtres annee et intervalle de dates permettent de restreindre le calcul.
- Le benefice net est calcule par la formule : total entrees - total depenses.

### 2.6 Creation d'une ligne de tresorerie

- L'endpoint `POST /api/back/statistiques/tresorerie` cree un enregistrement dans la table `tresorerie`.
- Le mouvement peut etre rattache a une commande via `idcommande`.
- Si `dateOperation` est absente, la date courante est utilisee.

## 3. Criteres de validation

- `year` doit etre un entier valide lorsque le filtre est utilise.
- `idproduit` doit etre un entier valide lorsqu'un produit est selectionne.
- `dateDebut` et `dateFin` doivent etre des dates valides au format ISO ou `yyyy-MM-dd`.
- Pour la creation d'une tresorerie, `typeMouvement` doit correspondre a une valeur enum existante.
- Pour la creation d'une tresorerie, `montant` doit etre numerique et strictement superieur a 0.
- Si `idcommande` est fourni, il doit referencer une commande existante.
- Les calculs de statistiques ne doivent pas renvoyer de valeurs invalides ; en cas d'absence de donnees, les vues doivent afficher 0 ou une liste vide.

## 4. Roles autorises

- Aucun controle de role n'est implemente explicitement dans le code actuel pour le module statistiques.
- Les pages et API sont donc accessibles a tout utilisateur qui peut atteindre l'application.
- Regle metier recommandee : restreindre l'acces aux profils administrateur, gestionnaire ou responsable financier si une securisation est ajoutee par la suite.

## 5. Remarques techniques

- Les vues utilisent Thymeleaf et Chart.js.
- Les calculs sont realises cote serveur dans `StatistiqueServiceImpl`.
- Les periodes non fournies sont completees par des bornes par defaut.