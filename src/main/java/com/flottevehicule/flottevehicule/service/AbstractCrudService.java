package com.flottevehicule.flottevehicule.service;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service abstrait de base pour les entités de l'application.
 * Il implémente {@link CrudService} et centralise les utilitaires de pagination et constantes.
 */
public abstract class AbstractCrudService implements CrudService {

    @SuppressWarnings("rawtypes")
    protected final JpaRepository repository;

    @SuppressWarnings("rawtypes")
    public AbstractCrudService(JpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Borne la taille de page demandée entre 1 et {@link #TAILLE_MAX}.
     *
     * @param taille la taille de page transmise par le client
     * @return la taille de page autorisée
     */
    protected int tailleAutorisee(int taille) {
        if (taille < 1) {
            return TAILLE_DEFAUT;
        }
        return Math.min(taille, TAILLE_MAX);
    }

    /**
     * Normalise l'index de page en ramenant toute valeur négative à 0.
     *
     * @param page l'index de page transmis par le client
     * @return l'index de page autorisé
     */
    protected int pageAutorisee(int page) {
        return Math.max(page, 0);
    }
}
