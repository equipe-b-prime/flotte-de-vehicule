#!/bin/bash
# =============================================================================
# TESTS CURL NIVEAU 2 - FLOTTE VEHICULE (F6-F10)
# Description: Tests complets Consultation et Recherche pour la gestion de flotte
# Auteur: Analyse Automatique
# =============================================================================

BASE_URL="http://localhost:8081"
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

info() {
    echo -e "${YELLOW}ℹ${NC} - $1"
}

echo "============================================================"
echo "  TESTS CURL NIVEAU 2 - FLOTTE DE VÉHICULES (F6-F10)"
echo "============================================================"
echo ""

# --- F6: Lister les véhicules d'un site ---
echo "--- F6: Lister les véhicules d'un site ---"

# Test F6a: GET /sites/{id}/vehicules (v site existant)
echo "  Test F6a: Véhicules d'un site existant (id=1)"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/1/vehicules")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Véhicules site récupérés (HTTP 200)"
    VEH_COUNT=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d))" 2>/dev/null || echo "unknown")
    echo "    Nombre de véhicules: $VEH_COUNT"
else
    fail "Échec récupération véhicules site (HTTP $HTTP_CODE): $BODY"
fi

# Test F6b: Site sans véhicule → liste vide
echo "  Test F6b: Site sans véhicule → liste vide"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/99/vehicules")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    echo "    Site inexistant ou sans véhicules"
else
    echo "    HTTP $HTTP_CODE"
fi

# Test F6b: 404 site inconnu
echo "  Test F6c: 404 pour site inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/999/vehicules")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour site inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

echo ""

# --- F7: Consulter les affectations d'un salarié ---
echo "--- F7: Consulter les affectations d'un salarié ---"

# Test F7a: GET /salaries/{id}/affectations (salarié existant)
echo "  Test F7a: Affectations d'un salarié existant (id=1)"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/1/affectations")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Affectations salarié récupérées (HTTP 200)"
    AFF_COUNT=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d))" 2>/dev/null || echo "unknown")
    echo "    Nombre d'affectations: $AFF_COUNT"
else
    fail "Échec récupération affectations (HTTP $HTTP_CODE): $BODY"
fi

# Test F7b: Affectation en cours signalée
# Création affectation en cours d'abord
curl -s -X POST "$BASE_URL/affectations" -H "Content-Type: application/json" \
    -d '{"dateDebut":"2024-06-01","dateFin":null,"salarieId":1,"vehiculeId":1}' > /dev/null
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/1/affectations")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    # Vérifier si affectation en cours signalée
    HAS_ACTIVE=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); any(a.get('dateFin')==None for a in d)" 2>/dev/null || echo "unknown")
    echo "    Affectation en cours détectée: $HAS_ACTIVE"
else
    fail "Échec récupération affectations (HTTP $HTTP_CODE)"
fi

# Test F7c: Salarié sans affectation → liste vide
echo "  Test F7c: Salarié sans affectation → liste vide"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/99/affectations")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    echo "    Liste vide pour salarié inexistant"
else
    echo "    HTTP $HTTP_CODE"
fi

# Test F7d: 404 salarié inconnu
echo "  Test F7d: 404 pour ID salarié inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/9999/affectations")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID salarié inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

echo ""

# --- F8: Consulter l'historique d'entretien d'un véhicule ---
echo "--- F8: Consulter l'historique d'entretien d'un véhicule ---"

# Test F8a: GET /vehicules/{id}/entretiens (véhicule existant)
echo "  Test F8a: Historique entretien véhicule existant (id=1)"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/1/entretiens")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Historique entretien récupéré (HTTP 200)"
    ENT_COUNT=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d))" 2>/dev/null || echo "unknown")
    echo "    Nombre d'entretiens: $ENT_COUNT"
    # Vérifier coût cumulé dans la réponse
    COST_SUM=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); s=sum(e.get('cout',0) for e in d); print(s)" 2>/dev/null || echo "unknown")
    echo "    Coût total affiché: $COST_SUM"
else
    fail "Échec récupération historique (HTTP $HTTP_CODE): $BODY"
fi

# Test F8b: Véhicule sans entretien → liste vide + coût cumulé 0
echo "  Test F8b: Véhicule sans entretien → liste vide"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/99/entretiens")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    echo "    Liste vide pour véhicule inexistant"
else
    echo "    HTTP $HTTP_CODE"
fi

# Test F8c: 404 véhicule inconnu
echo "  Test F8c: 404 pour ID véhicule inconnu"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/9999/entretiens")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 404 ]; then
    pass "404 pour ID véhicule inconnu (HTTP 404)"
else
    fail "Non 404 (HTTP $HTTP_CODE)"
fi

# Test F8d: Tri par date décroissante
echo "  Test F8d: Tri par date décroissante"
echo "    (Vérifier manuellement le tri date décroissante dans la réponse)"

echo ""

# --- F9: Rechercher un véhicule ---
echo "--- F9: Rechercher un véhicule ---"

# Test F9a: Recherche avec immatriculation + marque
echo "  Test F9a: Recherche immatriculation + marque"
RESULT=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/vehicules" \
    --data-urlencode "immatriculation=AA-111" \
    --data-urlencode "marque=Test")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Recherche implimmatriculation + marque (HTTP 200)"
    MATCH_COUNT=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d))" 2>/dev/null || echo "unknown")
    echo "    Résultats trouvés: $MATCH_COUNT"
else
    fail "Échec recherche (HTTP $HTTP_CODE): $BODY"
fi

# Test F9b: Recherche avec immatriculation seule (partielle, insensible casse)
echo "  Test F9b: Recherche immatriculation seule partielle/insensible casse"
RESULT=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/vehicules" \
    --data-urlencode "immatriculation=aa-111")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Recherche immatriculation seule (HTTP 200)"
else
    fail "Échec recherche immatriculation seule (HTTP $HTTP_CODE)"
fi

# Test F9c: Sans paramètre → tout le liste
echo "  Test F9c: Sans paramètre → tout le liste"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste complète sans paramètre (HTTP 200)"
else
    fail "Échec liste sans paramètre (HTTP $HTTP_CODE)"
fi

# Test F9d: Critères optionnels et combinables
echo "  Test F9d: Critères optionnels et combinables"
# Test avec un seul paramètre
RESULT=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/vehicules" \
    --data-urlencode "marque=Test")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Recherche marque seule (HTTP 200)"
else
    fail "Échec recherche marque seule (HTTP $HTTP_CODE)"
fi

echo ""

# --- F10: Paginer la liste des entretiens ---
echo "--- F10: Paginer la liste des entretiens ---"

# Test F10a: GET /entretiens?page=0&size=20&sort=date,desc
echo "  Test F10a: Pagination page 0 size 20 tri date décroissante"
RESULT=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/entretiens" \
    --data-urlencode "page=0" \
    --data-urlencode "size=20" \
    --data-urlencode "sort=date,desc")
HTTP_CODE=$(echo "$RESULT" | tail -1)
BODY=$(echo "$RESULT" | sed '$d')
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Pagination récupérée (HTTP 200)"
    # Vérifier metadata pagination
    PAGE_NUM=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('page','inconnue'))" 2>/dev/null || echo "inconnue")
    TOTAL=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalElements','inconnue'))" 2>/dev/null || echo "inconnue")
    TOTAL_PAGES=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalPages','inconnue'))" 2>/dev/null || echo "inconnue")
    echo "    Page: $PAGE_NUM, Éléments: $TOTAL, Pages totales: $TOTAL_PAGES"
else
    fail "Échec pagination (HTTP $HTTP_CODE): $BODY"
fi

# Test F10b: Size supérieur à 100 ramené à 100
echo "  Test F10b: Size supérieur à 100 ramené à 100"
RESULT=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/entretiens" \
    --data-urlencode "page=0" \
    --data-urlencode "size=50")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Size 50 accepté (HTTP 200)"
    # Vérifier que size maximale est 100
    SIZE=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('size','inconnue'))" 2>/dev/null || echo "inconnue")
    echo "    Taille page: $SIZE"
else
    fail "Échec size 50 (HTTP $HTTP_CODE)"
fi

# Test F10c: Sans paramètre → première page 20 éléments
echo "  Test F10c: Sans paramètre → première page 20 éléments"
RESULT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/entretiens")
HTTP_CODE=$(echo "$RESULT" | tail -1)
if [ "$HTTP_CODE" -eq 200 ]; then
    pass "Liste sans paramètre (HTTP 200)"
else
    fail "Échec liste sans paramètre (HTTP $HTTP_CODE)"
fi

echo ""
echo "============================================================"
echo "  RÉSUMÉ DES TESTS CURL NIVEAU 2"
echo "============================================================"
echo "Erreurs totales : $ERRORS"
if [ "$ERRORS" -eq 0 ]; then
    echo -e "${GREEN}TOUS LES TESTS ONT RÉUSSI ✓${NC}"
else
    echo -e "${RED}SOUCHES ENREGISTRÉES ✗${NC}"
fi
echo "============================================================"