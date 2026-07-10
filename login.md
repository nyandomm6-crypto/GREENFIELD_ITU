# Login unifié — GreenField

Document du flux d'authentification après passage à un **login unifié**
(une seule page `/login`, un seul formulaire pour clients ET employés).

---

## 1. Le problème de départ

En saisissant `admin@gmail.com` / `admin123` sur `http://localhost:8090/login`,
le message **« Email ou mot de passe incorrect »** apparaissait.

Cause : il existait **deux systèmes d'authentification séparés** :

| Page | URL | Contrôleur | Table consultée |
|------|-----|-----------|-----------------|
| Login **client** | `/login` | `AuthClientController` | table `client` |
| Login **employé** | `/emp/login` | `AuthEmployeController` | table `employes` |

Or `admin@gmail.com` vit dans la table **`employes`**, pas dans **`client`**.
La page `/login` interrogeait uniquement `client` → aucun résultat → erreur.

> Note : **le mot de passe n'est pas haché**. Il est stocké en clair et comparé
> avec `.equals()`. Le problème n'était donc pas le hash, mais la mauvaise table.

---

## 2. La solution : login unifié

Une seule page `/login`. À la soumission, le système :

1. cherche l'email **d'abord parmi les employés** ;
2. sinon **parmi les clients** ;
3. redirige selon le type de compte et le rôle.

### Ordre de vérification
Employé d'abord, client ensuite. Ainsi le personnel accède toujours à son
back-office même en cas d'email identique dans les deux tables (cas rare).

---

## 3. Flux détaillé (POST `/login`)

```
Formulaire /login (email + motDePasse)
        │
        ▼
┌─────────────────────────────────────────┐
│ 1. employesService.findByEmail(email)    │
│    email trouvé ET mot de passe correct ?│
└─────────────────────────────────────────┘
        │ oui                        │ non
        ▼                            ▼
  session["employe"] = employe   ┌────────────────────────────────────┐
  redirectSelonRole():           │ 2. clientRepository.findByMail()   │
    - Administrateur → /commandes/list   │  email trouvé ET mdp correct ? │
    - Caissier       → /caissier/dashboard└────────────────────────────────────┘
    - Livreur        → /livreurs/dashboard   │ non              │ oui
    - autre          → /login                ▼                  ▼
                                    erreur "Email ou      compte vérifié ?
                                    mot de passe          - non → erreur "valider email"
                                    incorrect"            - oui → session["client"]
                                                                  + rattache panier
                                                                  → redirect ou /
```

### Clés de session
- Employé connecté : attribut de session **`"employe"`** (constante `AuthGuard.SESSION_KEY`).
- Client connecté : attribut de session **`"client"`**.

Les contrôleurs back-office lisent `"employe"` via `AuthGuard.current(session)`
pour autoriser l'accès.

---

## 4. Ce qui a changé dans le code

### `AuthClientController.java` (le cœur)
- Injection de `EmployesService` dans le constructeur.
- `POST /login` réécrit : tentative **employé** puis **client**.
- Nouvelle méthode privée `redirectSelonRole(employe, redirect)` qui reprend la
  logique de redirection par rôle (reprise de l'ancien `AuthEmployeController`).

### `AuthEmployeController.java` (vidé)
- **Plus de logique de login.** L'ancien `POST /emp/login` (qui interrogeait la
  table `employes`) a été **supprimé**.
- Il ne reste que `GET /emp/login` qui **redirige vers `/login`** (en propageant
  le paramètre `redirect`).
- Raison : ~30 contrôleurs back-office font encore `return "redirect:/emp/login"`
  quand la session employé a expiré. Cette redirection les fait atterrir sur le
  formulaire unifié sans devoir modifier ces 30 lignes.

---

## 5. « Ça change quoi ? Il y a encore 2 login ? »

**Non.** Il n'existe plus qu'**UN SEUL traitement de connexion** : le `POST /login`.

- Avant : 2 formulaires + 2 traitements POST (`/login` et `/emp/login`).
- Après : 1 formulaire + 1 traitement POST (`/login`).
- `/emp/login` n'est plus qu'une **porte de redirection** (GET → `/login`), gardée
  seulement pour compatibilité avec les redirections back-office existantes.

Le template `back/auth/login.html` n'est plus affiché (page morte). On peut le
supprimer plus tard ; il ne gêne pas.

---

## 6. Comment se connecter maintenant

Une seule adresse pour tout le monde : **`http://localhost:8090/login`**

| Compte | Email | Mot de passe | Redirigé vers |
|--------|-------|--------------|---------------|
| Admin | `admin@gmail.com` | `admin123` | `/commandes/list` |
| Caissier | `caissier@gmail.com` | `caissier123` | `/caissier/dashboard` |
| Livreur | `livreur@gmail.com` | `livreur123` | `/livreurs/dashboard` |
| Client | (compte inscrit) | — | `/` (accueil) |
