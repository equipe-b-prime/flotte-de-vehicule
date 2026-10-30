# 🚗 La Flotte de Véhicules — API REST

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migration-red.svg)](https://flywaydb.org/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-yellow.svg)](https://swagger.io/)
[![Hosting](https://img.shields.io/badge/Render-Online-46E3B7.svg)](https://render.com/)
[![Database Cloud](https://img.shields.io/badge/Neon-PostgreSQL%20Cloud-00E599.svg)](https://neon.tech/)

Application d'entreprise robuste de **gestion de parc automobile** développée en **Spring Boot 4.1.1** et **Java 21**. Elle couvre l'intégralité du cycle de vie des véhicules d'une entreprise : gestion multi-sites, affectations conducteurs, contrats (LLD / Achat), maintenance & entretiens, alertes automatiques et restitution analytique avancée via des vues SQL optimisées.

---

## 🌐 Environnements & Liens d'Accès

L'application est entièrement déployée dans le Cloud avec une base PostgreSQL managée, et reste également exécutable en local :

| Environnement | URL de Base | Documentation Interactive Swagger UI | Spécification OpenAPI JSON | Base de Données |
| :--- | :--- | :--- | :--- | :--- |
| **🚀 En ligne (Render)** | `https://flotte-de-vehicule.onrender.com` | [Swagger UI Cloud](https://flotte-de-vehicule.onrender.com/swagger-ui.html) <br> *(ou avec ancre : [Swagger Entretiens](https://flotte-de-vehicule.onrender.com/swagger-ui/index.html#/Entretiens/getAllEntretiens))* | [`/v3/api-docs`](https://flotte-de-vehicule.onrender.com/v3/api-docs) | **Neon PostgreSQL** *(Cloud Serverless)* |
| **💻 En local** | `http://localhost:8090` | [Swagger UI Local](http://localhost:8090/swagger-ui.html) | [`/v3/api-docs`](http://localhost:8090/v3/api-docs) | **PostgreSQL Local** *(port 5432)* |

> 💡 **Astuce d'accès Swagger :**  
> L'interface Swagger UI est accessible aussi bien via l'URL simplifiée **`/swagger-ui.html`** que via l'URL complète avec ancre de navigation **`/swagger-ui/index.html#/Entretiens/getAllEntretiens`**.

---

## 🗄️ Bases de Données & Migrations Automatiques (Flyway)

Le projet intègre un système de migration versionnée **Flyway** garantissant une stricte cohérence de schéma entre l'environnement **local** et la base **Neon Cloud** :

```
src/main/resources/db/migration/
├── V1.0.1__DDL.sql   → Création des tables, contraintes, index et vues SQL
└── V1.0.2__DML.sql   → Référentiels fixes, données de test et recalibrage des séquences
```

### 1. Vues SQL Analytiques (`@Immutable`)
Pour garantir des performances optimales sans surcharger le code Java, 3 vues SQL natives sont automatiquement orchestrées par Flyway :
- **`v_vehicule_stats`** : Bilan consolidé par véhicule (nb affectations, nb entretiens, coût total, jours d'immobilisation, dates premier et dernier entretien).
- **`v_vehicule_cout_type`** : Répartition analytique des coûts par type d'intervention (Vidange, Pneumatiques, Freinage...).
- **`v_km_site_mensuel`** : Agrégation mensuelle des kilomètres parcourus par véhicule et rattachés au site.

### 2. Configuration (`application.properties`)
La configuration détecte automatiquement les variables d'environnement sur Render tout en proposant des valeurs par défaut pour le travail en local :
```properties
server.port=${PORT:8090}
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/flotte_vehicule}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

---

## 📦 Pièce Jointe : Collection Postman (`postman.json`)

Le projet fournit en pièce jointe directement à la racine la collection Postman prête à l'emploi :

📁 **`postman.json`** *(également disponible sous le nom `ZKANTAPI.postman_collection.json`)*

### Caractéristiques de la collection :
- **Couverture exhaustive** de l'ensemble des 19 fonctionnalités (F1 à F19).
- **Tous les cURL pré-configurés** et organisés par module (Sites, Salariés, Véhicules, Affectations, Entretiens, Restitution).
- **Variable globale `{{baseUrl}}`** permettant de basculer instantanément :
  - **Test en Local** : `http://localhost:8090`
  - **Test en Ligne (Cloud Render)** : `https://flotte-de-vehicule.onrender.com`
- Variables d'ID pré-remplies (`{{siteId}}`, `{{vehiculeId}}`, `{{salarieId}}`, `{{affectationId}}`).

### 🚀 Comment importer et utiliser `postman.json` dans Postman :
1. Lancez **Postman**.
2. Cliquez sur le bouton **Import** en haut à gauche.
3. Glissez-déposez le fichier **[`postman.json`](file:///home/yokan/TAF/FORMATION/TESTO/flottevehicule/postman.json)** (ou `ZKANTAPI.postman_collection.json`).
4. Dans l'onglet **Variables** de la collection :
   - Pour tester en local : laissez `baseUrl` sur `http://localhost:8090`.
   - Pour tester sur Render : mettez `baseUrl` sur `https://flotte-de-vehicule.onrender.com`.
5. Exécutez vos requêtes ou lancez le **Collection Runner** pour exécuter l'ensemble des tests en un clic.

---

## ⚡ Exemples de Tests cURL (En ligne & En local)

Vous pouvez exécuter directement des requêtes cURL depuis votre terminal vers le serveur distant ou local :

### 1. Lister tous les sites (F1)
```bash
# En ligne (Render) :
curl -X GET "https://flotte-de-vehicule.onrender.com/sites" -H "Accept: application/json"

# En local :
curl -X GET "http://localhost:8090/sites" -H "Accept: application/json"
```

### 2. Consulter l'historique d'entretien d'un véhicule (F8)
```bash
# En ligne :
curl -X GET "https://flotte-de-vehicule.onrender.com/vehicules/1/entretiens"

# En local :
curl -X GET "http://localhost:8090/vehicules/1/entretiens"
```

### 3. Calculer le coût total d'un véhicule via vue SQL (F18)
```bash
# En ligne :
curl -X GET "https://flotte-de-vehicule.onrender.com/vehicules/1/cout"

# En local :
curl -X GET "http://localhost:8090/vehicules/1/cout"
```

### 4. Mesurer le kilométrage mensuel par site (F19)
```bash
# En ligne :
curl -X GET "https://flotte-de-vehicule.onrender.com/sites/1/kilometrage?mois=2026-04"

# En local :
curl -X GET "http://localhost:8090/sites/1/kilometrage?mois=2026-04"
```

### 5. Rechercher des véhicules disponibles sur une période (F17)
```bash
# En local :
curl -X GET "http://localhost:8090/vehicules/disponibles?du=01-11-2026%2000:00:00&au=05-11-2026%2023:59:59"
```

---

## 🧪 Scripts de Tests Automatisés (`tests/`)

Pour maintenir la racine du projet propre et professionnelle, tous les scripts de test curl automatisés sont isolés dans le répertoire dédié **`tests/`** et configurés dans le **`.gitignore`** :

```
tests/
├── test_crud_niveau1_complet.sh  → Tests exhaustifs Niveau 1 (CRUD F1-F5)
├── test_curl_niveau1.sh          → Suite rapide Niveau 1
├── test_curl_niveau2.sh          → Tests Niveau 2 (Consultation & Recherche F6-F10)
└── test_niveaux_3_4.sh           → Tests Niveaux 3 & 4 (Règles Métier F11-F16 & Restitution F17-F19)
```

### Exécution des scripts :
```bash
# 1. Tester en local (port 8090)
PORT=8090 ./tests/test_niveaux_3_4.sh
PORT=8090 ./tests/test_curl_niveau2.sh

# 2. Tester directement l'API en production sur Render
BASE_URL="https://flotte-de-vehicule.onrender.com" ./tests/test_niveaux_3_4.sh
```

---

## 📅 Format de Date Standardisé

Toutes les dates de l'API suivent rigoureusement le format d'échange suivant :

$$\text{Format : } \mathbf{dd-MM-yyyy\ HH:mm:ss}$$

Exemple : `"15-06-2026 14:30:00"`

---

## 🗺️ Cartographie Complète des Fonctionnalités (F1 à F19)

### 🔹 Niveau 1 — CRUD Complet (F1 à F5)
- **F1 — Sites (`/sites`)** : Création avec nom unique, consultation triée par ville puis nom, modification ville/adresse, suppression sécurisée (refusée si véhicules rattachés).
- **F2 — Véhicules (`/vehicules`)** : Immatriculation unique, contrôle de date de mise en circulation non future, liaison contrat et site.
- **F3 — Salariés (`/salaries`)** : Matricule unique, consultation ordonnée par nom, suppression protégée si affectation active.
- **F4 — Affectations (`/affectations`)** : Attribution de véhicule à un salarié avec vérification de disponibilité.
- **F5 — Entretiens (`/entretiens`)** : Enregistrement des révisions avec coût strictement positif et durée d'immobilisation.

### 🔹 Niveau 2 — Recherche & Consultation (F6 à F10)
- **F6** : Consultation des véhicules d'un site.
- **F7** : Historique et affectation active d'un salarié.
- **F8** : Historique d'entretien d'un véhicule avec coût total cumulé.
- **F9** : Moteur de recherche multicritères de véhicules (marque, modèle, immatriculation partielle insensible à la casse).
- **F10** : Pagination et tri des entretiens par date décroissante.

### 🔹 Niveau 3 — Règles Métier Avancées (F11 à F16)
- **F11** : Détection et interdiction des chevauchements d'affectations sur un même véhicule (`HTTP 409 Conflict`).
- **F12** : Refus strict d'affectation pendant une période d'immobilisation pour entretien (`HTTP 409 Conflict`).
- **F13** : Clôture formelle et restitution de véhicule avec contrôle d'antériorité et état.
- **F14** : Contrôle d'intégrité kilométrique croissant (rejet des compteurs décroissants `HTTP 400`).
- **F15** : Algorithme de projection et calcul de la prochaine échéance d'entretien.
- **F16** : Détection des alertes d'entretien par tâche planifiée asynchrone (`@Scheduled`).

### 🔹 Niveau 4 — Restitution & Vues SQL (F17 à F19)
- **F17 — `/vehicules/disponibles`** : Recherche dynamique des véhicules libres sur une fenêtre temporelle donnée (par défaut : semaine en cours), groupés par site.
- **F18 — `/vehicules/{id}/cout`** : Analyse financière détaillée d'un véhicule (coût total, coût par kilomètre et ventilation par type de prestation via la vue `v_vehicule_cout_type`).
- **F19 — `/sites/{id}/kilometrage`** : Tableau de bord kilométrique mensuel par site exploitant la vue `v_km_site_mensuel`.

---

## 🚦 Codes d'État HTTP Retournés

| Code HTTP | Statut | Cas d'Usage |
| :---: | :--- | :--- |
| **`200 OK`** | Succès | Opération réussie avec corps de réponse. |
| **`201 Created`** | Créé | Ressource créée avec succès. |
| **`400 Bad Request`** | Requête Invalide | Kilométrage décroissant, date incohérente, coût négatif. |
| **`404 Not Found`** | Non Trouvé | Identifiant de ressource inexistant. |
| **`409 Conflict`** | Conflit Métier | Doublon d'immatriculation/matricule, chevauchement d'affectation, suppression bloquée. |
| **`500 Internal Error`** | Erreur Serveur | Erreur inattendue capturée par le Handler Global. |

---

## 💻 Démarrage en Développement Local

### Prérequis
- **Java 21**
- **PostgreSQL 16** (avec base `flotte_vehicule`)

### Lancement
```bash
# 1. Cloner le projet
git clone https://github.com/kanteyouss/flotte-de-vehicule.git
cd flotte-de-vehicule

# 2. Compiler
./mvnw clean compile

# 3. Lancer l'application Spring Boot
./mvnw spring-boot:run
```

L'application démarre automatiquement sur **`http://localhost:8090`**, Flyway applique les scripts DDL/DML, et Swagger UI est immédiatement disponible sur [http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html).
