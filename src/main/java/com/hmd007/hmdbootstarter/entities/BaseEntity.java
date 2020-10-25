package com.hmd007.hmdbootstarter.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.ToString;

/**
 * @author Hamad NJIMOLUH
 * Entité de base que les autres entités du système devraient hériter pour profiter des champs createdAt, updatedAt et deletedAt
 * et même de la logique métier derrière.
 *
 */
@MappedSuperclass
@Data @ToString
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    // ce champ sera l'état d'une entité visibilité et effectivité ou non
    private boolean blocked;
    // ce champ enregistrera automatiquement (grâce à SPRING BOOT) la date de création de l'entité en BD
    @CreationTimestamp
    private LocalDateTime createdAt;
    // ce champ enregistrera automatiquement (grâce à SPRING BOOT) la date de modification de l'entité en BD
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // ce champ permettra d'enregistrer la date à laquelle l'enité a été supprimé
    private LocalDateTime deletedAt;
}
