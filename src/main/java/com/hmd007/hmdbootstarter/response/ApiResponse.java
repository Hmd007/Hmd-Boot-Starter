package com.hmd007.hmdbootstarter.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hamad NJIMOLUH
 *
 * Classe qui permet gérer toutes les réponses de l'API REST.
 * @param <T> Classe de l'objet qui constituera le corps de la réponse
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int status = 200;
    private String message = "OK";
    private T data;
}
