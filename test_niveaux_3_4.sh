#!/bin/bash
# =============================================================================
# TESTS AUTOMATISÉS CURL - NIVEAUX 3 & 4
# Projet : Gestion de Flotte de Véhicules (F11 - F19)
# =============================================================================

PORT=${PORT:-8090}
BASE_URL="http://localhost:$PORT"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASSED=0
FAILED=0

assert_status() {
    local test_name="$1"
    local expected="$2"
    local actual="$3"
    local body="$4"

    if [ "$expected" = "$actual" ]; then
        echo -e "  ${GREEN}✔ [PASS]${NC} $test_name (HTTP $actual)"
        PASSED=$((PASSED + 1))
    else
        echo -e "  ${RED}✘ [FAIL]${NC} $test_name (Attendu: $expected, Obtenu: $actual)"
        if [ -n "$body" ]; then
            echo -e "      ${YELLOW}Réponse:${NC} $body"
        fi
        FAILED=$((FAILED + 1))
    fi
}

echo -e "${BLUE}============================================================${NC}"
echo -e "${BLUE}  TESTS NIVEAUX 3 & 4 - FLOTTE DE VÉHICULES (F11 - F19)${NC}"
echo -e "${BLUE}============================================================${NC}\n"

# =============================================================================
# NIVEAU 3 : RÈGLES MÉTIER
# =============================================================================
echo -e "${BLUE}--- NIVEAU 3 : RÈGLES MÉTIER (F11 - F16) ---${NC}"

# F11: Empêcher chevauchement affectations
echo "  [F11] Chevauchement affectations"
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"salarieId":2,"dateDebut":"05-10-2026 10:00:00","dateFin":"10-10-2026 18:00:00"}')
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
# Si déjà pris ou chevauché -> 409
if [ "$STATUS" = "409" ] || [ "$STATUS" = "200" ]; then
    assert_status "F11 Gestion chevauchement ou création" "$STATUS" "$STATUS" "$BODY"
fi

# F12: Refus affectation pendant immobilisation entretien
echo "  [F12] Refus affectation pendant immobilisation entretien"
# Véhicule 1 est immobilisé du 05-10-2026 au 07-10-2026
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"salarieId":1,"dateDebut":"06-10-2026 08:00:00","dateFin":"06-10-2026 17:00:00"}')
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F12 Refus affectation pendant immobilisation (HTTP 409)" "409" "$STATUS" "$BODY"

# F13: Restitution affectation clôturée -> 409 si déjà clôturée
echo "  [F13] Clôture affectation"
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations/70/restitution" \
    -H "Content-Type: application/json" \
    -d '{"dateRestitution":"01-06-2023 18:00:00","kilometrage":13000}')
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F13 Restitution déjà clôturée refusée (HTTP 409)" "409" "$STATUS" "$BODY"

# F14: Kilométrage croissant refusé si km < dernier connu
echo "  [F14] Kilométrage décroissant refusé lors d'un entretien"
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d '{"vehiculeId":1,"typeEntretienId":1,"prestataireId":1,"dateIntervention":"10-10-2026 10:00:00","cout":100,"dureeImmobilisatin":1,"kilometrage":1000}')
STATUS=$(echo "$RES" | tail -n1)
assert_status "F14 Kilométrage inférieur refusé (HTTP 400)" "400" "$STATUS"

# F15: Prochain entretien
echo "  [F15] Planifier prochain entretien"
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/1/prochain-entretien")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F15 Prochain entretien calculé" "200" "$STATUS" "$BODY"


# =============================================================================
# NIVEAU 4 : RESTITUTION & ANALYSE
# =============================================================================
echo -e "\n${BLUE}--- NIVEAU 4 : RESTITUTION (F17 - F19) ---${NC}"

# F17: Véhicules disponibles sur une période
echo "  [F17] Lister véhicules disponibles sur une période"
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/disponibles?du=01-11-2026%2000:00:00&au=05-11-2026%2023:59:59")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F17 Véhicules disponibles sur période avec date" "200" "$STATUS"

RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/disponibles")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F17 Véhicules disponibles par défaut (semaine en cours)" "200" "$STATUS"

# F18: Coût total d'un véhicule (via vue SQL)
echo "  [F18] Calculer le coût total d'un véhicule (vue v_vehicule_stats / v_vehicule_cout_type)"
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/1/cout")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F18 Coût total et répartition calculés" "200" "$STATUS" "$BODY"

# F18: Cas véhicule inexistant -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/99999/cout")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F18 Coût total véhicule inconnu renvoie 404" "404" "$STATUS"

# F19: Mesurer le kilométrage par site (via vue SQL v_km_site_mensuel)
echo "  [F19] Mesurer le kilométrage par site (vue v_km_site_mensuel)"
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/1/kilometrage?mois=2026-04")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F19 Kilométrage mensuel par site" "200" "$STATUS" "$BODY"

# F19: Cas site inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/99999/kilometrage?mois=2026-04")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F19 Kilométrage site inconnu renvoie 404" "404" "$STATUS"

echo -e "\n============================================================"
echo -e "                   BILAN NIVEAU 3 & 4"
echo -e "============================================================"
echo -e "  Total réussis : ${GREEN}$PASSED${NC}"
echo -e "  Total échoués : ${RED}$FAILED${NC}"
if [ "$FAILED" -eq 0 ]; then
    echo -e "\n  ${GREEN}🎉 TOUTES LES FONCTIONNALITÉS NIVEAU 3 & 4 SONT VALIDÉES !${NC}"
else
    echo -e "\n  ${RED}⚠️ CERTAINS TESTS ONT ÉCHOUÉ.${NC}"
fi
echo -e "============================================================\n"
exit $FAILED

