# Routes existantes du projet GreenField

## 🖥️ Routes Front (Client)

### Authentification

| Méthode | URL                 | Description                       | Contrôleur           |
| ------- | ------------------- | --------------------------------- | -------------------- |
| GET     | `/login`            | Afficher page de connexion        | AuthClientController |
| POST    | `/login`            | Traiter la connexion client       | AuthClientController |
| GET     | `/signup`           | Afficher page d'inscription       | AuthClientController |
| POST    | `/signup`           | Traiter l'inscription client      | AuthClientController |
| GET     | `/validation/email` | Afficher page de validation email | AuthClientController |
| POST    | `/validation/email` | Vérifier le code de validation    | AuthClientController |
| POST    | `/validation`       | Validation AJAX (JSON)            | AuthClientController |
| POST    | `/logout`           | Déconnexion client                | AuthClientController |
| GET     | `/logout`           | Déconnexion client (GET)          | AuthClientController |
| GET     | `/profil`           | Afficher profil client            | AuthClientController |
| GET     | `/profil/edit`      | Formulaire modification profil    | AuthClientController |
| POST    | `/profil/edit`      | Mettre à jour profil              | AuthClientController |

### Accueil

| Méthode | URL | Description                                               | Contrôleur                |
| ------- | --- | --------------------------------------------------------- | ------------------------- |
| GET     | `/` | Page d'accueil avec best-sellers, nouveaux produits, etc. | DashboardClientController |

### Panier

| Méthode | URL                             | Description                      | Contrôleur       |
| ------- | ------------------------------- | -------------------------------- | ---------------- |
| GET     | `/panier`                       | Afficher le panier               | PanierController |
| POST    | `/panier/ajouter`               | Ajouter produit au panier (AJAX) | PanierController |
| POST    | `/panier/modifier`              | Modifier quantité                | PanierController |
| POST    | `/panier/supprimer/{idProduit}` | Supprimer produit du panier      | PanierController |

### Commande

| Méthode | URL                       | Description                    | Contrôleur           |
| ------- | ------------------------- | ------------------------------ | -------------------- |
| GET     | `/commande/recapitulatif` | Récapitulatif avant validation | CommandeController   |
| POST    | `/commande/valider`       | Valider la commande            | CommandeController   |
| GET     | `/commandes`              | Liste des commandes du client  | AuthClientController |
| GET     | `/commandes/{id}`         | Détail d'une commande          | AuthClientController |

### Produits

| Méthode | URL              | Description                  | Contrôleur        |
| ------- | ---------------- | ---------------------------- | ----------------- |
| GET     | `/produits`      | Liste des produits (filtres) | ProduitController |
| GET     | `/produits/{id}` | Détail d'un produit          | ProduitController |

### Email

| Méthode | URL      | Description              | Contrôleur      |
| ------- | -------- | ------------------------ | --------------- |
| GET     | `/email` | Formulaire d'envoi email | EmailController |
| POST    | `/email` | Envoyer email            | EmailController |

---

## 🔒 Routes Back (Employés)

### Authentification Employé

| Méthode | URL          | Description                  | Contrôleur            |
| ------- | ------------ | ---------------------------- | --------------------- |
| GET     | `/emp/login` | Page de connexion employé    | AuthEmployeController |
| POST    | `/emp/login` | Traiter la connexion employé | AuthEmployeController |

### Dashboard Livreur

| Méthode | URL                                          | Description                    | Contrôleur                 |
| ------- | -------------------------------------------- | ------------------------------ | -------------------------- |
| GET     | `/livreurs/dashboard`                        | Dashboard livreur              | DashboardLivreurController |
| GET     | `/livreurs/profil`                           | Profil livreur                 | DashboardLivreurController |
| GET     | `/livreurs/livraisons`                       | Mes livraisons                 | DashboardLivreurController |
| GET     | `/livreurs/livraisons/{id}`                  | Détail livraison               | DashboardLivreurController |
| GET     | `/livreurs/historique-livraisons`            | Historique des livraisons      | DashboardLivreurController |
| POST    | `/livreurs/livraisons/valider`               | Valider une livraison          | DashboardLivreurController |
| POST    | `/livraisonFille/valider`                    | Valider une ligne de livraison | DashboardLivreurController |
| GET     | `/livreurs/livraisons/paiement/{idCommande}` | Page de paiement livraison     | DashboardLivreurController |
| POST    | `/paiements/ajouter-multiple`                | Ajouter plusieurs paiements    | DashboardLivreurController |

### Gestion Livraison (Admin)

| Méthode | URL                             | Description                   | Contrôleur          |
| ------- | ------------------------------- | ----------------------------- | ------------------- |
| GET     | `/livraison`                    | Liste des livraisons          | LivraisonController |
| GET     | `/livraison/create`             | Formulaire création livraison | LivraisonController |
| POST    | `/livraison/create`             | Créer une livraison           | LivraisonController |
| GET     | `/livraison/{id}`               | Détail livraison              | LivraisonController |
| GET     | `/livraisons/filter`            | Filtrer les livraisons (AJAX) | LivraisonController |
| POST    | `/livraison-fille/annuler/{id}` | Annuler une ligne (AJAX)      | LivraisonController |
| POST    | `/livraison-fille/valider/{id}` | Valider une ligne (AJAX)      | LivraisonController |
| POST    | `/livraison-fille/reporter`     | Reporter une ligne            | LivraisonController |

### Gestion Point de Vente

| Méthode | URL                                  | Description                     | Contrôleur             |
| ------- | ------------------------------------ | ------------------------------- | ---------------------- |
| GET     | `/pointdevente`                      | Liste des points de vente       | PointDeVenteController |
| GET     | `/pointdevente/ajouter`              | Formulaire ajout point de vente | PointDeVenteController |
| POST    | `/pointdevente/ajouter`              | Créer point de vente            | PointDeVenteController |
| GET     | `/pointdevente/{id}`                 | Détail point de vente           | PointDeVenteController |
| GET     | `/pointdevente/{id}/modifier`        | Formulaire modification         | PointDeVenteController |
| POST    | `/pointdevente/{id}/modifier`        | Modifier point de vente         | PointDeVenteController |
| POST    | `/pointdevente/{id}/supprimer`       | Supprimer point de vente        | PointDeVenteController |
| GET     | `/pointdevente/{id}/stock`           | Stock du point de vente         | PointDeVenteController |
| GET     | `/pointdevente/{id}/ajouter-produit` | Formulaire ajout produit        | PointDeVenteController |
| POST    | `/pointdevente/{id}/ajouter-produit` | Ajouter produit au stock        | PointDeVenteController |
| GET     | `/pointdevente/{id}/employes`        | Employés du point de vente      | PointDeVenteController |
| GET     | `/pointdevente/{id}/mouvements`      | Mouvements de stock             | PointDeVenteController |

### Gestion Véhicules

| Méthode | URL                         | Description               | Contrôleur         |
| ------- | --------------------------- | ------------------------- | ------------------ |
| GET     | `/vehicules`                | Liste des véhicules       | VehiculeController |
| GET     | `/vehicules/ajouter`        | Formulaire ajout véhicule | VehiculeController |
| POST    | `/vehicules/ajouter`        | Créer véhicule            | VehiculeController |
| GET     | `/vehicules/{id}`           | Détail véhicule           | VehiculeController |
| GET     | `/vehicules/{id}/modifier`  | Formulaire modification   | VehiculeController |
| POST    | `/vehicules/{id}/modifier`  | Modifier véhicule         | VehiculeController |
| POST    | `/vehicules/{id}/supprimer` | Supprimer véhicule        | VehiculeController |

---

## 🔗 Utilitaires / Transversaux

| Type                | Description                                                    | Contrôleur                 |
| ------------------- | -------------------------------------------------------------- | -------------------------- |
| `@ControllerAdvice` | NavigationControllerAdvice - Badge panier dans toutes les vues | NavigationControllerAdvice |

---

## 📊 Synthèse

| Catégorie          | Nombre de routes |
| ------------------ | ---------------- |
| **Front (Client)** | 25               |
| **Back (Employé)** | 31               |
| **Total**          | **56**           |

## 📋 Résumé par Contrôleur

| Contrôleur                 | Routes | Type        |
| -------------------------- | ------ | ----------- |
| AuthClientController       | 12     | Front       |
| DashboardClientController  | 1      | Front       |
| PanierController           | 4      | Front       |
| CommandeController         | 2      | Front       |
| ProduitController          | 2      | Front       |
| EmailController            | 2      | Front       |
| AuthEmployeController      | 2      | Back        |
| DashboardLivreurController | 9      | Back        |
| LivraisonController        | 8      | Back        |
| PointDeVenteController     | 12     | Back        |
| VehiculeController         | 7      | Back        |
| NavigationControllerAdvice | -      | Transversal |
