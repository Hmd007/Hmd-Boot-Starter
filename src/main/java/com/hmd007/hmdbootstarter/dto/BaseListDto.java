package com.hmd007.hmdbootstarter.dto;

import com.hmd007.hmdbootstarter.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hamad NJIMOLUH
 *
 * Classe de base pour la liste des DTOs permettant de construire les listes des DTOs du système à partir d'un objet DTO
 * indirectement lié à une entité dont ce dernier (l'objet DTO) a le paramètre.
 * @param <T> Le DTO associé à l'entité dont on souhaite construire la liste des DTOs
 */
@Component
public class BaseListDto<T> extends ApiResponse<BaseListDto.PagedItemList<T>> {
    private static final long serialVersionUID = 1L;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class PagedItemList<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        List<T> items;
        int totalPages;
        long totalElements;
    }
}
