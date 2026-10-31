package com.flottevehicule.flottevehicule.dto;

/**
 * Classe de base pour le nettoyage des chaînes de caractères dans les DTOs de requête.
 * Supprime les espaces superflus (début, fin, et espaces multiples consécutifs)
 * pour garantir l'unicité et la cohérence des données.
 */
public abstract class AbstractClean {

    protected String cleanText(String text) {
        return text != null ? text.replaceAll("\\s+", " ").trim() : null;
    }
}
