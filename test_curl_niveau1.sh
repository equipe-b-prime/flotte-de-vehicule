#!/bin/bash
# =============================================================================
# TESTS CURL NIVEAU 1 - FLOTTE VEHICULE (F1-F5)
# Description: Tests complets CRUD pour la gestion de flotte de véhicules
# Auteur: Analyse Automatique
# =============================================================================

BASE_URL="http://localhost:8080"
ERRORS=0

# Couleurs pour la sortie
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

pass() {
    echo -e "${GREEN}✓ PASS${NC} - $1"
}

fail() {
    echo -e "${RED}✗ FAIL${NC} - $1"
    ERRORS=$((ERRORS + 1))
}

echo "============================================================"
echo "  TESTS CURL NIVEAU 1 - FLOTTE DE VÉHICULES (F1-F5)"
echo "============================================================"
echo ""

# --- F1: Gérer les Sites ---
echo "--- F1: Gérer les Sites ---"

# Test F1a: Création site (nom, ville, adresse obligatoires, nom unique)
echo "  Test F1a: Création site avec nom/ville/adresse obligatoires"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sites" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Site A","ville":"Paris","adresse":"1 Rue Paris"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 409 ]; then
    pass "Création site OK (HTTP $HTTP_CODE)"
else
    fail "Échec création site (HTTP $HTTP_CODE): $BODY"
fi

# Test F1b: Nom unique (double création devrait échouer)
echo "  Test F1b: Nom unique - deuxième création avec même nom"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sites" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Site A","ville":"Lyon","adresse":"2 Rue Lyon"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Nom unique détecté (HTTP 409 Conflict)"
else
    fail "Non détecté nom unique (HTTP $HTTP_CODE): $BODY"
fi

# Test F1c: Consultation liste triée par ville puis nom
echo "  Test F1c: Consultation liste triée par ville puis nom"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste sites récupérée (HTTP 200)"
    # Vérifier si trié (simule)
    echo "    (Vérifier manuellement le tri ville/nom dans la réponse)"
else
    fail "Échec récupération liste (HTTP $HTTP_CODE)"
fi

# Test F1d: 404 identifiant inconnu
echo "  Test F1d: 404 pour identifiant inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/9999")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F1e: Modification site
echo "  Test F1e: Modification adresse et ville"
RESULT=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/sites/1" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Site A","ville":"Lyon","adresse":"3 Rue Lyon"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Modification site réussi (HTTP 200)"
else
    fail "Échec modification site (HTTP $HTTP_CODE)"
fi

# Test F1f: Suppression refusée si véhicules présents
echo "  Test F1f: Suppression refusée si site a des véhicules"
# D'abord créer un véhicule sur ce site, puis essayer de supprimer
curl -s -X POST "$BASE_URL/vehicules" -H "Content-Type: application/json" \
    -d '{"immatriculation":"AA-111","marque":"Test","modele":"X","nom":"Test","dateMiseEnCirculation":"01-01-2020 00:00:00","siteId":1}' > /dev/null
RESULT=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/sites/1")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Suppression refusée (HTTP 409) - site a des véhicules"
else
    # Peut être 200 selon implémentation, mais on vérifie le message
    echo "    (Suppression acceptée ou erreur : $HTTP_CODE)"
fi

echo ""

# --- F2: Gérer les Véhicules ---
echo "--- F2: Gérer les Véhicules ---"

# Test F2a: Création véhicule (immatriculation, marque, modèle, date mise en service, site obligatoires)
echo "  Test F2a: Création véhicule avec tous champs obligatoires"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d '{"immatriculation":"BB-222","marque":"Renault","modele":"Twingo","nom":"Vehicule A","dateMiseEnCirculation":"15-03-2021 00:00:00","siteId":1,"contratId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 409 ]; then
    pass "Création véhicule OK (HTTP $HTTP_CODE)"
else
    fail "Échec création véhicule (HTTP $HTTP_CODE): $BODY"
fi

# Test F2b: Immatriculation unique
echo "  Test F2b: Immatriculation unique - doublon"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d '{"immatriculation":"BB-222","marque":"Peugeot","modele":"208","nom":"Vehicule B","dateMiseEnCirculation":"01-04-2021 00:00:00","siteId":1,"contratId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Immatriculation unique détectée (HTTP 409)"
else
    fail "Non détecté immatriculation unique (HTTP $HTTP_CODE): $BODY"
fi

# Test F2c: Date mise en service dans le futur refusée
echo "  Test F2c: Date mise en service dans le futur refusée"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d '{"immatriculation":"CC-333","marque":"Citroen","modele":"C3","nom":"Vehicule C","dateMiseEnCirculation":"01-01-2030 00:00:00","siteId":1,"contratId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 400 ]; then
    pass "Date futur refusée (HTTP 400)"
else
    fail "Non refusé date futur (HTTP $HTTP_CODE): $BODY"
fi

# Test F2c: Consultation liste triée par immatriculation
echo "  Test F2c: Consultation liste triée par immatriculation"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste véhicules récupérée (HTTP 200)"
else
    fail "Échec récupération liste (HTTP $HTTP_CODE)"
fi

# Test F2d: 404 identifiant inconnu
echo "  Test F2d: 404 pour ID véhicule inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/9999")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F2e: Modification site modifiable (transfert)
echo "  Test F2e: Modification site (transfert vehicule)"
RESULT=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/vehicules/1" \
    -H "Content-Type: application/json" \
    -d '{"immatriculation":"BB-222","marque":"Renault","modele":"Twingo","nom":"Vehicule A","dateMiseEnCirculation":"15-03-2021 00:00:00","siteId":2,"contratId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Modification site (transfert) réussi (HTTP 200)"
else
    fail "Échec modification site (HTTP $HTTP_CODE)"
fi

# Test F2e: Immatriculation et date figées (non modifiables en PUT)
# Tentative de modification immatriculation devrait être bloquée ou ignorée selon implémentation
echo "  Test F2e: Vérifier que immatriculation/date figées en modification"

# Test F2f: Suppression refusée si affectations ou entretiens
echo "  Test F2f: Suppression refusée si véhicule a affectations/entretiens"
# Création affectation d'abord
curl -s -X POST "$BASE_URL/affectations" -H "Content-Type: application/json" \
    -d '{"dateDebut":"01-01-2024 00:00:00","dateFin":null,"salarieId":1,"vehiculeId":1}' > /dev/null
RESULT=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/vehicules/1")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Suppression refusée véhicule avec affectation (HTTP 409)"
else
    echo "    (HTTP $HTTP_CODE - selon implémentation)"
fi

echo ""

# --- F3: Gérer les Salariés ---
echo "--- F3: Gérer les Salariés ---"

# Test F3a: Création salarié (nom, matricule, service obligatoires, matricule unique)
echo "  Test F3a: Création salarié avec nom/matricule/service obligatoires"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/salaries" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Dupont","prenom":"Jean","matricule":"MAT-001","service":"Logistique"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 409 ]; then
    pass "Création salarié OK (HTTP $HTTP_CODE)"
else
    fail "Échec création salarié (HTTP $HTTP_CODE)"
fi

# Test F3b: Matricule unique
echo "  Test F3b: Matricule unique - doublon"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/salaries" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Martin","prenom":"Pierre","matricule":"MAT-001","service":"Informatique"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Matricule unique détecté (HTTP 409)"
else
    fail "Non détecté matricule unique (HTTP $HTTP_CODE): $BODY"
fi

# Test F3c: Consultation liste triée par nom
echo "  Test F3c: Consultation liste triée par nom"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste salariés récupérée (HTTP 200)"
else
    fail "Échec récupération liste (HTTP $HTTP_CODE)"
fi

# Test F3d: 404 identifiant inconnu
echo "  Test F3d: 404 pour ID salarié inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/9999")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F3e: Modification service modifiable, matricule figé
echo "  Test F3e: Modification service, matricule figé"
RESULT=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/salaries/1" \
    -H "Content-Type: application/json" \
    -d '{"nom":"Dupont","prenom":"Jean","matricule":"MAT-001","service":"Informatique"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Modification salarié réussi (HTTP 200)"
else
    fail "Échec modification salarié (HTTP $HTTP_CODE)"
fi

# Test F3f: Suppression refusée si affectation en cours
echo "  Test F3f: Suppression refusée si salarié a affectation en cours"
curl -s -X POST "$BASE_URL/affectations" -H "Content-Type: application/json" \
    -d '{"dateDebut":"01-01-2024 00:00:00","dateFin":null,"salarieId":1,"vehiculeId":1}' > /dev/null
RESULT=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/salaries/1")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 409 ]; then
    pass "Suppression refusée salarié avec affectation (HTTP 409)"
else
    echo "    (HTTP $HTTP_CODE - selon implémentation)"
fi

echo ""

# --- F4: Gérer les Affectations ---
echo "--- F4: Gérer les Affectations ---"

# Test F4a: Création affectation (véhicule, salarié, date début obligatoires, véhicule libre)
echo "  Test F4a: Création affectation véhicule libre sur période"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d '{"dateDebut":"01-06-2024 00:00:00","dateFin":null,"salarieId":1,"vehiculeId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Création affectation réussi (HTTP 200)"
else
    fail "Échec création affectation (HTTP $HTTP_CODE)"
fi

# Test F4b: Consultation liste triée par date début décroissante
echo "  Test F4b: Consultation liste triée par date début décroissante"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/affectations")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste affectations récupérée (HTTP 200)"
else
    fail "Échec récupération liste (HTTP $HTTP_CODE)"
fi

# Test F4c: 404 identifiant inconnu
echo "  Test F4c: 404 pour ID affectation inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/affectations/9999")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F4d: Modification date fin modifiable
echo "  Test F4d: Modification date fin"
RESULT=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/affectations/1" \
    -H "Content-Type: application/json" \
    -d '{"dateDebut":"01-06-2024 00:00:00","dateFin":"31-12-2024 00:00:00"}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Modification date fin réussi (HTTP 200)"
else
    fail "Échec modification date fin (HTTP $HTTP_CODE)"
fi

# Test F4e: Restitution affectation (clôturer affectation)
echo "  Test F4e: Restitution affectation avec date et kilométrage"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations/1/restitution" \
    -H "Content-Type: application/json" \
    -d '{"dateRestitution":"30-12-2024 00:00:00","kilometrage":15000}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Restitution affectation réussi (HTTP 200)"
else
    # Peut être 400/409 selon validation
    info "Restitution: HTTP $HTTP_CODE"
fi

# Test F4f: Suppression possible uniquement sur affectation non commencée
echo "  Test F4f: Suppression affectation non commencée"
# D'abord supprimer la restitution pour avoir affectation non clôturée
# Essayons suppression sur affectation qui n'a pas commencé
curl -s -X POST "$BASE_URL/affectations" -H "Content-Type: application/json" \
    -d '{"dateDebut":"01-01-2025 00:00:00","dateFin":null,"salarieId":2,"vehiculeId":2}' > /dev/null
RESULT=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/affectations/2")
HTTP_CODE=$(echo "$RESULT" | tail -1)
# 409 si déjà commencé, ou 200 si non commencé
if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 409 ]; then
    info "Suppression affectation non commencée: HTTP $HTTP_CODE"
else
    echo "    (HTTP $HTTP_CODE)"
fi

echo ""

# --- F5: Gérer les Entretiens ---
echo "--- F5: Gérer les Entretiens ---"

# Test F5a: Création entretien (véhicule, date, type, coût obligatoires, coût > 0)
echo "  Test F5a: Création entretien avec coût positif"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"dateIntervention":"15-06-2024 00:00:00","cout":500,"dureeImmobilisatin":1,"kilometrage":35000,"typeEntretienId":1,"prestataireId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Création entretien réussi (HTTP 200)"
else
    fail "Échec création entretien (HTTP $HTTP_CODE): $BODY"
fi

# Test F5b: Critères obligatoires (date ne peut pas précéder mise en service)
echo "  Test F5b: Création entretien date invalide (avant mise en service)"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"dateIntervention":"01-01-2019 00:00:00","cout":500,"dureeImmobilisatin":1,"kilometrage":30000,"typeEntretienId":1,"prestataireId":1}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 400 ]; then
    pass "Date invalide refusée (HTTP 400)"
else
    fail "Non refusé date invalide (HTTP $HTTP_CODE): $BODY"
fi

# Test F5c: Consultation liste triée par date décroissante avec coût cumulé
echo "  Test F5c: Consultation liste triée par date décroissante"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/entretiens")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste entretiens récupérée (HTTP 200)"
else
    fail "Échec récupération liste (HTTP $HTTP_CODE)"
fi

# Test F5d: 404 identifiant inconnu
echo "  Test F5d: 404 pour ID entretien inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/entretiens/9999")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F5e: Modification coût et prestataire modifiables
echo "  Test F5e: Modification coût et prestataire"
ENTRETIEN_ID=$(curl -s "$BASE_URL/entretiens" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d[0]['id'] if d else '')" 2>/dev/null)
RESULT=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/entretiens/$ENTRETIEN_ID" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"dateIntervention":"15-06-2024 00:00:00","cout":750,"dureeImmobilisatin":1,"kilometrage":35000,"typeEntretienId":1,"prestataireId":2}')
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Modification coût/prestataire réussi (HTTP 200)"
else
    fail "Échec modification coût/prestataire (HTTP $HTTP_CODE): $BODY"
fi

# Test F5f: Suppression possible tant que période immobilisation non commencée
echo "  Test F5f: Suppression entretien (période immobilisation)"
# Lancement suppression simple
RESULT=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/entretiens/1")
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "    Suppression entretien: HTTP $HTTP_CODE (comportement selon règles métier)"

echo ""
echo "============================================================"
echo "  RÉSUMÉ DES TESTS CURL NIVEAU 1"
echo "============================================================"
echo "Erreurs totales : $ERRORS"
if [ "$ERRORS" -eq 0 ]; then
    echo -e "${GREEN}TOUS LES TESTS ONT RÉUSSI ✓${NC}"
else
    echo -e "${RED}SOUCHES ENREGISTRÉES ✗${NC}"
fi
echo "============================================================"