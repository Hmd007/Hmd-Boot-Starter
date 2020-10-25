package com.hmd007.hmdbootstarter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Hamad NJIMOLUH
 *
 * Interface de récupération des items sous forme de page.
 * @param <T> Type du item dont on souhaite avoir la page des items
 */
public interface PageFetcher<T> {
    /**
     * Cette méthode sera rédefinie par chaque classe l'implementant, ceci avec sa propre logique de pagination et
     * retourne la page avec les items.
     * @param pageable objet de pagination ayant des champs page, size et sort servant à paginé
     * @return La liste des items sous forme de page
     */
    Page<T> getItems(Pageable pageable);
}
