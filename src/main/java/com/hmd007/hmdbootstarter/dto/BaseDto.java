package com.hmd007.hmdbootstarter.dto;

import com.hmd007.hmdbootstarter.entities.BaseEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Hamad NJIMOLUH
 *
 * Classe de base DTO permettant de construire tous les DTO du système ceci basé sur un principe de conversion que
 * chaque DTO d'une entité précise rédefinira.
 *
 * @param <T> Classe Entité à envoyé au DTO pour mappage
 */
@Data @ToString
@Component
public abstract class BaseDto<T extends BaseEntity> {
    private boolean blocked;
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime deletedAt;

    /**
     * A partir de l'entité, cette méthode crée un nouveau DTO.
     *
     * @param entity Objet de l'entité dont on souhaite créer le DTO
     * @return DTO de l'objet entité envoyé en paramètre
     */
    public abstract BaseDto<T> getDtoFromEntity(T entity);

    /**
     * Transforme le DTO en entité.
     *
     * @return Entité du DTO ayant appélé la méthode
     */
    public abstract T convertToEntity();

    /**
     * A partir de la liste des entités, cette méthode crée une liste des DTOs.
     *
     * @param entities Liste des objets de l'entité dont on souhaite créer la liste des DTOs
     * @return La liste des DTOs des objets entités envoyé en paramètre
     */
    public abstract List<BaseDto<T>> getListDtoFromListEntity(List<T> entities);

}
