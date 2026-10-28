# La Flotte de Véhicules — API REST

Application Spring Boot de gestion de flotte de véhicules : sites, véhicules, salariés, affectations et entretiens.

## 🚀 Démarrage rapide

### Prérequis
- Java 21
- Maven (via le wrapper `./mvnw`)
- PostgreSQL (base `flotte_vehicule`)

### Configuration base de données
Fichier : `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/flotte_vehicule
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### Lancer l'application
```bash
./mvnw clean compile
./mvnw spring-boot:run
```
Le serveur démarre sur `http://localhost:8080`.

## 📅 Format de date

Toutes les dates sont au format **billetterie** : `dd-MM-yyyy HH:mm:ss`

Exemple : `"01-06-2024 10:30:00`

## 🌿 Structure des branches

| Branche | Niveau | Contenu |
|---------|--------|---------|
| `KANTAPI-N1` | Niveau 1 | CRUD complet F1-F5 |
| `KANTAPI-N2` | Niveau 2 | Consultation et recherche F6-F10 |
| `KANTAPI-N3` | Niveau 3 | Règles métier F11-F16 |
| `KANTAPI-N4` | Niveau 4 | Restitution F17-F19 |
| `V1`, `V2`, `V3` | Versions antérieures | Historique |
| `main` | Production | État stable |

## 🧪 Tests

### Scripts curl

| Script | Niveau | Couverture |
|--------|--------|------------|
| `test_curl_niveau1.sh` | Niveau 1 | CRUD F1-F5 (28 tests) |
| `test_curl_niveau2.sh` | Niveau 2 | Consultation F6-F10 (25 tests) |

### Exécution
```bash
# Serveur démarré requis
./test_curl_niveau1.sh
./test_curl_niveau2.sh
```

### Tests unitaires
```bash
./mvnw test
```

## 📁 Architecture

```
src/main/java/com/flottevehicule/flottevehicule/
├── controller/     → Endpoints REST
├── service/        → Logique métier
├── repository/     → Accès données (Spring Data JPA)
├── dto/            → Objets Request/Response
├── entity/         → Entités JPA
├── exception/      → Exceptions métier + handler global
└── utils/response/ → Enveloppe de réponse standard
```

## 🔌 Endpoints — Niveau 1 (CRUD F1-F5)

### F1 — Sites (`/sites`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/sites` | Créer un site (nom/ville/adresse obligatoires, nom unique) |
| GET | `/sites` | Lister les sites (tri ville puis nom) |
| GET | `/sites/{id}` | Consulter un site (404 si inconnu) |
| PUT | `/sites/{id}` | Modifier (adresse et ville) |
| DELETE | `/sites/{id}` | Supprimer (refusé si véhicules présents) |

### F2 — Véhicules (`/vehicules`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/vehicules` | Créer (immatriculation unique, date pas future) |
| GET | `/vehicules` | Lister (tri immatriculation) |
| GET | `/vehicules/{id}` | Consulter (404 si inconnu) |
| PUT | `/vehicules/{id}` | Modifier (site modifiable, immatriculation/date figées) |
| DELETE | `/vehicules/{id}` | Supprimer (refusé si affectations/entretiens) |

### F3 — Salariés (`/salaries`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/salaries` | Créer (matricule unique) |
| GET | `/salaries` | Lister (tri nom) |
| GET | `/salaries/{id}` | Consulter (404 si inconnu) |
| PUT | `/salaries/{id}` | Modifier (service modifiable, matricule figé) |
| DELETE | `/salaries/{id}` | Supprimer (refusé si affectation en cours) |

### F4 — Affectations (`/affectations`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/affectations` | Créer (véhicule libre, date ≥ mise en service) |
| GET | `/affectations` | Lister (tri date début décroissante) |
| GET | `/affectations/{id}` | Consulter (404 si inconnu) |
| PUT | `/affectations/{id}` | Modifier (date fin modifiable) |
| DELETE | `/affectations/{id}` | Supprimer (seulement si non commencée) |

### F5 — Entretiens (`/entretiens`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/entretiens` | Créer (coût > 0, km > dernier connu) |
| GET | `/entretiens` | Lister (tri date décroissante + coût cumulé) |
| GET | `/entretiens/{id}` | Consulter (404 si inconnu) |
| PUT | `/entretiens/{id}` | Modifier (coût/prestataire, date/km figés) |
| DELETE | `/entretiens/{id}` | Supprimer (si immobilisation non commencée) |

## 📊 Codes de réponse

| Code | Signification |
|------|---------------|
| 200 | Succès |
| 400 | Requête invalide (mauvaise date, coût ≤ 0, km décroissant) |
| 404 | Ressource introuvable |
| 409 | Conflit (doublon, suppression refusée, chevauchement) |

## 🔧 Problèmes résolus (historique)

- **`ResponseDto`** : transformée en interface pour permettre l'implémentation par les DTOs de réponse
- **Format de date** : uniformisé au format billetterie `dd-MM-yyyy HH:mm:ss` via `@JsonFormat`
- **Repository kilométrage** : remplacé `findTopByVehiculeIdOrderByKilometrageDesc` (retournait l'entité) par `@Query SELECT MAX(e.kilometrage)` (retourne `Integer`)

## 📄 Contraintes d'architecture

- **Aucune nouvelle table** de base de données
- **Aucune nouvelle relation** entre tables
- **Attributs ajoutés** uniquement sur les entités existantes (ex: `kilometrage`, `kilometrageRestitution`)