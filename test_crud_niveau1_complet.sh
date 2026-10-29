#!/bin/bash
# =============================================================================
# TESTS AUTOMATISÉS CURL - NIVEAU 1 : LE CRUD (F1 - F5)
# Projet : Gestion de Flotte de Véhicules
# =============================================================================

PORT=${PORT:-8080}
BASE_URL="http://localhost:$PORT"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASSED=0
FAILED=0

# Vérifier si l'API répond
echo -e "${BLUE}=== VÉRIFICATION DU SERVEUR SUR $BASE_URL ===${NC}"
HTTP_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/sites" || echo "DOWN")
if [ "$HTTP_CHECK" = "DOWN" ] || [ "$HTTP_CHECK" = "000" ]; then
    echo -e "${RED}[ERREUR] Le serveur flottevehicule n'est pas démarré sur le port $PORT !${NC}"
    echo "Lancez-le avec : ./mvnw spring-boot:run"
    exit 1
fi
echo -e "${GREEN}[OK] Serveur en ligne.${NC}\n"

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

TIMESTAMP=$(date +%s)

# =============================================================================
# F1. GÉRER LES SITES
# =============================================================================
echo -e "${BLUE}------------------------------------------------------------${NC}"
echo -e "${BLUE}  F1. GESTION DES SITES${NC}"
echo -e "${BLUE}------------------------------------------------------------${NC}"

# 1. Création site 1
SITE_NOM_1="Site Alpha $TIMESTAMP"
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sites" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"$SITE_NOM_1\",\"ville\":\"Lyon\",\"adresse\":\"10 rue des Fleurs\"}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
SITE_ID_1=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F1.1 Création site 1 valide" "200" "$STATUS" "$BODY"

# 2. Doublon sur le nom du site -> 409
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sites" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"$SITE_NOM_1\",\"ville\":\"Paris\",\"adresse\":\"20 rue Blanche\"}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
assert_status "F1.2 Unicité nom du site (doublon interdit)" "409" "$STATUS" "$BODY"

# 3. Création site 2 pour tester le tri
SITE_NOM_2="Site Beta $TIMESTAMP"
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sites" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"$SITE_NOM_2\",\"ville\":\"Bordeaux\",\"adresse\":\"5 quai des Chartrons\"}")
STATUS=$(echo "$RES" | tail -n1)
SITE_ID_2=$(echo "$RES" | sed '$d' | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F1.3 Création site 2 valide" "200" "$STATUS"

# 4. Consultation liste sites (triée ville puis nom)
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.4 Consultation liste des sites" "200" "$STATUS"

# 5. Consultation site par ID
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/$SITE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.5 Consultation site par ID existant" "200" "$STATUS"

# 6. Consultation ID inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/sites/999999")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.6 Consultation site ID inconnu renvoie 404" "404" "$STATUS"

# 7. Modification adresse et ville
RES=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/sites/$SITE_ID_1" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"$SITE_NOM_1\",\"ville\":\"Lyon Modifie\",\"adresse\":\"99 av de Saxe\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.7 Modification ville et adresse" "200" "$STATUS"

# 8. Suppression site sans véhicule -> 200
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/sites/$SITE_ID_2")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.8 Suppression site sans véhicule" "200" "$STATUS"


# =============================================================================
# F2. GÉRER LES VÉHICULES
# =============================================================================
echo -e "\n${BLUE}------------------------------------------------------------${NC}"
echo -e "${BLUE}  F2. GESTION DES VÉHICULES${NC}"
echo -e "${BLUE}------------------------------------------------------------${NC}"

IMMAT_1="AA-${TIMESTAMP: -4}-XX"
# 1. Date dans le futur refusée -> 400
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d "{\"immatriculation\":\"$IMMAT_1\",\"marque\":\"Peugeot\",\"modele\":\"208\",\"nom\":\"P208\",\"dateMiseEnCirculation\":\"01-01-2099 00:00:00\",\"siteId\":$SITE_ID_1}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.1 Date de mise en service dans le futur interdite (400)" "400" "$STATUS"

# 2. Création valide
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d "{\"immatriculation\":\"$IMMAT_1\",\"marque\":\"Peugeot\",\"modele\":\"208\",\"nom\":\"P208\",\"dateMiseEnCirculation\":\"01-01-2022 10:00:00\",\"siteId\":$SITE_ID_1}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
VEHICULE_ID_1=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F2.2 Création véhicule valide" "200" "$STATUS" "$BODY"

# 3. Unicité de l'immatriculation -> 409
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicules" \
    -H "Content-Type: application/json" \
    -d "{\"immatriculation\":\"$IMMAT_1\",\"marque\":\"Renault\",\"modele\":\"Clio\",\"nom\":\"Clio\",\"dateMiseEnCirculation\":\"01-01-2022 10:00:00\",\"siteId\":$SITE_ID_1}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.3 Immatriculation unique (doublon interdit 409)" "409" "$STATUS"

# 4. Consultation véhicule par ID
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/$VEHICULE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.4 Consultation véhicule par ID existant" "200" "$STATUS"

# 5. Consultation véhicule ID inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/vehicules/999999")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.5 Consultation véhicule ID inconnu renvoie 404" "404" "$STATUS"

# 6. Modification du site (transfert)
RES=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/vehicules/$VEHICULE_ID_1" \
    -H "Content-Type: application/json" \
    -d "{\"siteId\":$SITE_ID_1,\"immatriculation\":\"TENTATIVE_CHANGE\",\"dateMiseEnCirculation\":\"01-01-2000 00:00:00\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.6 Modification / transfert site" "200" "$STATUS"

# 7. Suppression site refusée car il a un véhicule rattaché -> 409
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/sites/$SITE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F1.9 Suppression site refusée si véhicules rattachés (409)" "409" "$STATUS"


# =============================================================================
# F3. GÉRER LES SALARIÉS
# =============================================================================
echo -e "\n${BLUE}------------------------------------------------------------${NC}"
echo -e "${BLUE}  F3. GESTION DES SALARIÉS${NC}"
echo -e "${BLUE}------------------------------------------------------------${NC}"

MATRICULE_1="MAT-$TIMESTAMP"
# 1. Création salarié 1 valide
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/salaries" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"Dupont\",\"prenom\":\"Jean\",\"matricule\":\"$MATRICULE_1\",\"service\":\"Technique\"}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
SALARIE_ID_1=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F3.1 Création salarié valide" "200" "$STATUS" "$BODY"

# 2. Matricule unique -> 409
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/salaries" \
    -H "Content-Type: application/json" \
    -d "{\"nom\":\"Martin\",\"prenom\":\"Sophie\",\"matricule\":\"$MATRICULE_1\",\"service\":\"RH\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F3.2 Matricule unique (doublon interdit 409)" "409" "$STATUS"

# 3. Consultation salarié par ID
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/$SALARIE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F3.3 Consultation salarié par ID" "200" "$STATUS"

# 4. Consultation ID inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/salaries/999999")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F3.4 Consultation salarié ID inconnu renvoie 404" "404" "$STATUS"

# 5. Modification service (matricule figé)
RES=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/salaries/$SALARIE_ID_1" \
    -H "Content-Type: application/json" \
    -d "{\"service\":\"Informatique\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F3.5 Modification service du salarié" "200" "$STATUS"


# =============================================================================
# F4. GÉRER LES AFFECTATIONS
# =============================================================================
echo -e "\n${BLUE}------------------------------------------------------------${NC}"
echo -e "${BLUE}  F4. GESTION DES AFFECTATIONS${NC}"
echo -e "${BLUE}------------------------------------------------------------${NC}"

# 1. Date début avant mise en service du véhicule -> 400
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"salarieId\":$SALARIE_ID_1,\"dateDebut\":\"01-01-2020 00:00:00\",\"dateFin\":\"01-06-2020 00:00:00\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.1 Date début avant mise en circulation refusée (400)" "400" "$STATUS"

# 2. Création valide dans le futur (pour tester suppression avant démarrage)
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"salarieId\":$SALARIE_ID_1,\"dateDebut\":\"01-01-2090 00:00:00\",\"dateFin\":\"01-02-2090 00:00:00\"}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
AFFECTATION_FUTURE_ID=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F4.2 Création affectation future valide" "200" "$STATUS"

# 3. Chevauchement d'affectation sur le même véhicule -> 409
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"salarieId\":$SALARIE_ID_1,\"dateDebut\":\"15-01-2090 00:00:00\",\"dateFin\":\"15-02-2090 00:00:00\"}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.3 Chevauchement d'affectation refusé (409)" "409" "$STATUS"

# 4. Suppression autorisée car affectation non commencée -> 200
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/affectations/$AFFECTATION_FUTURE_ID")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.4 Suppression affectation non commencée" "200" "$STATUS"

# 5. Création affectation passée/en cours (commencée)
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"salarieId\":$SALARIE_ID_1,\"dateDebut\":\"01-01-2023 00:00:00\",\"dateFin\":null}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
AFFECTATION_ACTIVE_ID=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F4.5 Création affectation active" "200" "$STATUS"

# 6. Suppression refusée car déjà commencée -> 409
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/affectations/$AFFECTATION_ACTIVE_ID")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.6 Suppression affectation commencée refusée (409)" "409" "$STATUS"

# 7. Suppression salarié refusée car affectation en cours -> 409
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/salaries/$SALARIE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F3.6 Suppression salarié refusée si affectation en cours (409)" "409" "$STATUS"

# 8. Clôture par restitution
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/affectations/$AFFECTATION_ACTIVE_ID/restitution" \
    -H "Content-Type: application/json" \
    -d "{\"dateRestitution\":\"01-06-2023 18:00:00\",\"kilometrage\":12500}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.7 Restitution et clôture de l'affectation" "200" "$STATUS"

# 9. Consultation affectation ID inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/affectations/999999")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F4.8 Consultation affectation ID inconnu renvoie 404" "404" "$STATUS"


# =============================================================================
# F5. GÉRER LES ENTRETIENS
# =============================================================================
echo -e "\n${BLUE}------------------------------------------------------------${NC}"
echo -e "${BLUE}  F5. GESTION DES ENTRETIENS${NC}"
echo -e "${BLUE}------------------------------------------------------------${NC}"

# Récupérer un type d'entretien ou en créer/trouver un
TYPE_ENTRETIEN_ID=1
PRESTATAIRE_ID=1

# 1. Coût négatif ou nul refusé -> 400
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"typeEntretienId\":$TYPE_ENTRETIEN_ID,\"prestataireId\":$PRESTATAIRE_ID,\"dateIntervention\":\"01-07-2023 10:00:00\",\"cout\":-50,\"dureeImmobilisatin\":1,\"kilometrage\":15000}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.1 Coût négatif ou nul refusé (400)" "400" "$STATUS"

# 2. Date d'entretien avant mise en service du véhicule -> 400
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"typeEntretienId\":$TYPE_ENTRETIEN_ID,\"prestataireId\":$PRESTATAIRE_ID,\"dateIntervention\":\"01-01-2020 10:00:00\",\"cout\":150,\"dureeImmobilisatin\":1,\"kilometrage\":15000}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.2 Date entretien antérieure à la mise en service refusée (400)" "400" "$STATUS"

# 3. Création entretien valide
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"typeEntretienId\":$TYPE_ENTRETIEN_ID,\"prestataireId\":$PRESTATAIRE_ID,\"dateIntervention\":\"01-07-2023 10:00:00\",\"cout\":250.50,\"dureeImmobilisatin\":2,\"kilometrage\":20000}")
STATUS=$(echo "$RES" | tail -n1)
BODY=$(echo "$RES" | sed '$d')
ENTRETIEN_ID_1=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
assert_status "F5.3 Création entretien valide" "200" "$STATUS" "$BODY"

# 4. Kilométrage inférieur ou égal au dernier connu refusé -> 400
RES=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/entretiens" \
    -H "Content-Type: application/json" \
    -d "{\"vehiculeId\":$VEHICULE_ID_1,\"typeEntretienId\":$TYPE_ENTRETIEN_ID,\"prestataireId\":$PRESTATAIRE_ID,\"dateIntervention\":\"05-07-2023 10:00:00\",\"cout\":100,\"dureeImmobilisatin\":1,\"kilometrage\":18000}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.4 Kilométrage décroissant refusé (400)" "400" "$STATUS"

# 5. Consultation entretien par ID
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/entretiens/$ENTRETIEN_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.5 Consultation entretien par ID" "200" "$STATUS"

# 6. Consultation entretien ID inconnu -> 404
RES=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/entretiens/999999")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.6 Consultation entretien ID inconnu renvoie 404" "404" "$STATUS"

# 7. Modification du coût et prestataire
RES=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/entretiens/$ENTRETIEN_ID_1" \
    -H "Content-Type: application/json" \
    -d "{\"cout\":320.00,\"prestataireId\":$PRESTATAIRE_ID}")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F5.7 Modification coût/prestataire entretien" "200" "$STATUS"

# 8. Suppression véhicule refusée car il a des affectations/entretiens -> 409
RES=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/vehicules/$VEHICULE_ID_1")
STATUS=$(echo "$RES" | tail -n1)
assert_status "F2.7 Suppression véhicule refusée si affectations/entretiens (409)" "409" "$STATUS"


# =============================================================================
# BILAN
# =============================================================================
echo -e "\n============================================================"
echo -e "                   BILAN DES TESTS (F1 - F5)"
echo -e "============================================================"
echo -e "  Total réussis : ${GREEN}$PASSED${NC}"
echo -e "  Total échoués : ${RED}$FAILED${NC}"
if [ "$FAILED" -eq 0 ]; then
    echo -e "\n  ${GREEN}🎉 TOUTES LES RÈGLES MÉTIER ET CRUD F1-F5 SONT VALIDÉES !${NC}"
else
    echo -e "\n  ${RED}⚠️ CERTAINS TESTS ONT ÉCHOUÉ.${NC}"
fi
echo -e "============================================================\n"
exit $FAILED

