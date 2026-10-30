-- =========================================================================
-- V1.0.2__DML.sql : INITIALISATION DES DONNÉES DE RÉFÉRENCE ET JEU DE TEST
-- Projet : Gestion de Flotte de Véhicules (flottevehicule)
-- =========================================================================

-- =========================================================================
-- 1. NETTOYAGE DES TABLES (SANS SUPPRIMER LA STRUCTURE)
-- =========================================================================

TRUNCATE TABLE
    trajet,
    depense,
    entretien,
    affectation,
    vehicule,
    type_depense,
    type_entretien,
    prestataire,
    salarie,
    etat_affectation,
    contrat,
    site
RESTART IDENTITY CASCADE;

-- =========================================================================
-- 2. DONNÉES DE PARAMÉTRAGE / RÉFÉRENTIELS (PARAMÈTRES FIXES)
-- =========================================================================

-- États d'affectation
INSERT INTO etat_affectation (id, code, libelle) VALUES
    (1, 'DISPONIBLE', 'Disponible'),
    (2, 'AFFECTE', 'Affecté'),
    (3, 'EN_MAINTENANCE', 'En maintenance')
ON CONFLICT (id) DO UPDATE SET
    code = EXCLUDED.code,
    libelle = EXCLUDED.libelle;

-- Types d'entretien
INSERT INTO type_entretien (id, libelle, est_regulier) VALUES
    (1, 'Vidange', true),
    (2, 'Pneumatiques', true),
    (3, 'Freinage', true),
    (4, 'Révision générale', true),
    (5, 'Contrôle technique', true)
ON CONFLICT (id) DO UPDATE SET
    libelle = EXCLUDED.libelle,
    est_regulier = EXCLUDED.est_regulier;

-- Types de dépense
INSERT INTO type_depense (id, libelle) VALUES
    (1, 'Carburant'),
    (2, 'Péage'),
    (3, 'Entretien ponctuel'),
    (4, 'Parking')
ON CONFLICT (id) DO UPDATE SET
    libelle = EXCLUDED.libelle;

-- Prestataires
INSERT INTO prestataire (id, nom) VALUES
    (1, 'Auto Rapid'),
    (2, 'Garage Central'),
    (3, 'Speedy Services'),
    (4, 'Point S')
ON CONFLICT (id) DO UPDATE SET
    nom = EXCLUDED.nom;

-- Contrats
INSERT INTO contrat (id, type_contrat, duree_mois, loyer_mensuel, km_autorise) VALUES
    (1, 'LLD', 36, 250000.00, 20000),
    (2, 'Location longue durée', 24, 180000.00, 15000),
    (3, 'Achat', 0, 0.00, 0),
    (4, 'LLD', 48, 320000.00, 30000)
ON CONFLICT (id) DO UPDATE SET
    type_contrat = EXCLUDED.type_contrat,
    duree_mois = EXCLUDED.duree_mois,
    loyer_mensuel = EXCLUDED.loyer_mensuel,
    km_autorise = EXCLUDED.km_autorise;

-- =========================================================================
-- 3. SITES & SALARIÉS DE BASE
-- =========================================================================

-- Sites
INSERT INTO site (id, nom, ville, adresse) VALUES
    (1, 'Site Cocody', 'Abidjan', 'Boulevard de France, Cocody'),
    (2, 'Site Yopougon', 'Abidjan', 'Boulevard Principal, Yopougon'),
    (3, 'Site Bouaké', 'Bouaké', 'Avenue de la Paix, Bouaké'),
    (4, 'Site San-Pédro', 'San-Pédro', 'Zone Portuaire, San-Pédro'),
    (5, 'Site Yamoussoukro', 'Yamoussoukro', 'Boulevard FHB, Yamoussoukro')
ON CONFLICT (id) DO UPDATE SET
    nom = EXCLUDED.nom,
    ville = EXCLUDED.ville,
    adresse = EXCLUDED.adresse;

-- Salariés
INSERT INTO salarie (id, nom, prenom, matricule, service) VALUES
    (1, 'Kouassi', 'Jean', 'SAL-001', 'Informatique'),
    (2, 'Koné', 'Aminata', 'SAL-002', 'Commercial'),
    (3, 'Traoré', 'Moussa', 'SAL-003', 'Technique'),
    (4, 'Yao', 'Aya', 'SAL-004', 'RH'),
    (5, 'Ouattara', 'Ibrahim', 'SAL-005', 'Logistique'),
    (6, 'Diabaté', 'Fatou', 'SAL-006', 'Commercial'),
    (7, 'Bamba', 'Souleymane', 'SAL-007', 'Technique'),
    (8, 'N''Guessan', 'Adjoua', 'SAL-008', 'Direction')
ON CONFLICT (id) DO UPDATE SET
    nom = EXCLUDED.nom,
    prenom = EXCLUDED.prenom,
    matricule = EXCLUDED.matricule,
    service = EXCLUDED.service;

-- =========================================================================
-- 4. VÉHICULES
-- =========================================================================

INSERT INTO vehicule (id, nom, immatriculation, marque, modele, date_mise_en_circulation, site_id, contrat_id, etat_affectation_id, kilometrage_actuel) VALUES
    (1, 'Véhicule 1', 'CI-1001-MD', 'Peugeot', '208', '2022-01-15 08:00:00', 1, 1, 1, 25000),
    (2, 'Véhicule 2', 'CI-1002-MD', 'Renault', 'Clio', '2021-06-10 09:00:00', 1, 1, 1, 32000),
    (3, 'Véhicule 3', 'CI-1003-MD', 'Toyota', 'Hilux', '2023-03-20 10:00:00', 2, 2, 1, 18000),
    (4, 'Véhicule 4', 'CI-1004-MD', 'Dacia', 'Duster', '2020-11-05 08:30:00', 3, 3, 1, 65000),
    (5, 'Véhicule 5', 'CI-1005-MD', 'Hyundai', 'Tucson', '2023-07-01 11:00:00', 4, 4, 1, 12000)
ON CONFLICT (id) DO UPDATE SET
    nom = EXCLUDED.nom,
    immatriculation = EXCLUDED.immatriculation,
    marque = EXCLUDED.marque,
    modele = EXCLUDED.modele,
    date_mise_en_circulation = EXCLUDED.date_mise_en_circulation,
    site_id = EXCLUDED.site_id,
    contrat_id = EXCLUDED.contrat_id,
    etat_affectation_id = EXCLUDED.etat_affectation_id,
    kilometrage_actuel = EXCLUDED.kilometrage_actuel;

-- =========================================================================
-- 5. AFFECTATIONS & CLÔTURES
-- =========================================================================

INSERT INTO affectation (id, date_debut, date_fin, kilometrage_restitution, salarie_id, vehicule_id) VALUES
    (70, '2023-01-01 08:00:00', '2023-06-01 18:00:00', 13000, 1, 1),
    (71, '2026-04-01 08:00:00', '2026-04-30 18:00:00', 22000, 2, 1),
    (72, '2026-05-01 08:00:00', '2026-05-31 18:00:00', 24000, 3, 2)
ON CONFLICT (id) DO UPDATE SET
    date_debut = EXCLUDED.date_debut,
    date_fin = EXCLUDED.date_fin,
    kilometrage_restitution = EXCLUDED.kilometrage_restitution,
    salarie_id = EXCLUDED.salarie_id,
    vehicule_id = EXCLUDED.vehicule_id;

-- =========================================================================
-- 6. ENTRETIENS (IMMOBILISATION DU VÉHICULE 1 DU 05/10/2026 AU 07/10/2026)
-- =========================================================================

INSERT INTO entretien (id, date_intervention, cout, duree_immobilisatin, kilometrage, vehicule_id, type_entretien_id, prestataire_id) VALUES
    (1, '2026-10-05 09:00:00', 750.00, 2, 20000, 1, 1, 1),
    (2, '2026-01-15 09:00:00', 150.00, 1, 15000, 1, 2, 2),
    (3, '2026-03-20 09:00:00', 300.00, 1, 18000, 2, 1, 1)
ON CONFLICT (id) DO UPDATE SET
    date_intervention = EXCLUDED.date_intervention,
    cout = EXCLUDED.cout,
    duree_immobilisatin = EXCLUDED.duree_immobilisatin,
    kilometrage = EXCLUDED.kilometrage,
    vehicule_id = EXCLUDED.vehicule_id,
    type_entretien_id = EXCLUDED.type_entretien_id,
    prestataire_id = EXCLUDED.prestataire_id;

-- =========================================================================
-- 7. TRAJETS & DÉPENSES (POUR LES STATISTIQUES MENSUELLES F19)
-- =========================================================================

-- Trajets en avril 2026 pour le site 1 (véhicule 1)
INSERT INTO trajet (id, km_depart, km_arrivee, date_trajet, motif, affectation_id) VALUES
    (1, 10000, 10450, '2026-04-05 10:00:00', 'Mission client Cocody', 71),
    (2, 10450, 10900, '2026-04-18 14:00:00', 'Déplacement technique', 71),
    (3, 15000, 15300, '2026-05-10 09:00:00', 'Livraison matériel', 72)
ON CONFLICT (id) DO UPDATE SET
    km_depart = EXCLUDED.km_depart,
    km_arrivee = EXCLUDED.km_arrivee,
    date_trajet = EXCLUDED.date_trajet,
    motif = EXCLUDED.motif,
    affectation_id = EXCLUDED.affectation_id;

-- Dépenses associées
INSERT INTO depense (id, date_depense, montant_depense, type_depense_id, affectation_id) VALUES
    (1, '2026-04-05 11:30:00', 50.00, 1, 71),
    (2, '2026-04-18 15:00:00', 12.50, 2, 71)
ON CONFLICT (id) DO UPDATE SET
    date_depense = EXCLUDED.date_depense,
    montant_depense = EXCLUDED.montant_depense,
    type_depense_id = EXCLUDED.type_depense_id,
    affectation_id = EXCLUDED.affectation_id;

-- =========================================================================
-- 8. SYNCHRONISATION DES SÉQUENCES
-- =========================================================================

SELECT setval('site_id_seq', COALESCE((SELECT MAX(id) FROM site), 1) + 1, false);
SELECT setval('contrat_id_seq', COALESCE((SELECT MAX(id) FROM contrat), 1) + 1, false);
SELECT setval('etat_affectation_id_seq', COALESCE((SELECT MAX(id) FROM etat_affectation), 1) + 1, false);
SELECT setval('salarie_id_seq', COALESCE((SELECT MAX(id) FROM salarie), 1) + 1, false);
SELECT setval('vehicule_id_seq', COALESCE((SELECT MAX(id) FROM vehicule), 1) + 1, false);
SELECT setval('prestataire_id_seq', COALESCE((SELECT MAX(id) FROM prestataire), 1) + 1, false);
SELECT setval('type_entretien_id_seq', COALESCE((SELECT MAX(id) FROM type_entretien), 1) + 1, false);
SELECT setval('type_depense_id_seq', COALESCE((SELECT MAX(id) FROM type_depense), 1) + 1, false);
SELECT setval('affectation_id_seq', COALESCE((SELECT MAX(id) FROM affectation), 1) + 1, false);
SELECT setval('entretien_id_seq', COALESCE((SELECT MAX(id) FROM entretien), 1) + 1, false);
SELECT setval('depense_id_seq', COALESCE((SELECT MAX(id) FROM depense), 1) + 1, false);
SELECT setval('trajet_id_seq', COALESCE((SELECT MAX(id) FROM trajet), 1) + 1, false);
