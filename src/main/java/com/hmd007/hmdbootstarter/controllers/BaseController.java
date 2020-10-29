package com.hmd007.hmdbootstarter.controllers;

import com.hmd007.hmdbootstarter.dto.BaseDto;
import com.hmd007.hmdbootstarter.dto.BaseListDto;
import com.hmd007.hmdbootstarter.entities.BaseEntity;
import com.hmd007.hmdbootstarter.response.ApiResponse;
import com.hmd007.hmdbootstarter.service.PageFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author Hamad NJIMOLUH
 *
 * Controleur de base qui permet de générer automatiquement des endpoints prêt à l'utiliation pour chaque entité de base,
 * notamment des endpoints de liste, details, suppression et autres...
 *
 * @param <KEY> Clé de l'entité dont on souhaite avoir les endpoints
 * @param <ENTITY> Classe de l'entité
 * @param <DTO> Classe Dto de base de l'entité
 * @param <DETAIL_DTO> Classe Dto de l'entité utilisé dans le endpoint de détail sur l'entité
 * @param <LIST_DTO> Classe Dto de la liste des éléments de l'entité
 * @param <REPOSITORY> Interface de repository de l'entité
 */
public abstract class BaseController<
        KEY,
        ENTITY extends BaseEntity,
        DTO extends BaseDto<ENTITY>,
        DETAIL_DTO extends BaseDto<ENTITY>,
        LIST_DTO extends BaseListDto<DTO>,
        REPOSITORY extends JpaRepository<ENTITY, KEY>> {

    private boolean paginated;
    private DTO dto;
    private DETAIL_DTO detailDto;
    private REPOSITORY repository;

    public BaseController(boolean paginated, DTO dto, DETAIL_DTO detailDto, REPOSITORY repository) {
        this.paginated = paginated;
        this.dto = dto;
        this.detailDto = detailDto;
        this.repository = repository;
    }

    /**
     * Méthode de tri, à rédefinir ou pas par les classes héritant de cette classe
     * @return Objet de tri
     */
    public abstract Sort getSort();

    /**
     * Méthode de réponse générique aux appels API REST.
     *
     * @param data Corps de la réponse des réquêtes http
     * @param httpStatus Statut de la réponse http
     * @param <T> Classe de l'objet de la réponse à construire
     * @return ResponseEntity construit avec un corps @data et statut
     */
    public <T extends ApiResponse> ResponseEntity<T> output(T data, HttpStatus httpStatus) {
        return new ResponseEntity<>(data, httpStatus);
    }

    /**
     * Méthode avec réponse pour les requêtes envoyées sans corps
     * @return
     */
    protected ResponseEntity<ApiResponse<DTO>> responseBodyNotFound() {
        ApiResponse<DTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage("Le corps de la requête envoyée est nul.");
        return output(apiResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Méthode avec réponse pour les identifiants inexistants
     * @param id
     * @return
     */
    protected ResponseEntity<ApiResponse<DTO>> responseIdNotExist(KEY id) {
        ApiResponse<DTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
        apiResponse.setMessage("L'élement d'identifiant " + id + " récherché est inexistant.");
        return output(apiResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Méthode qui permet de récuperer la liste des entités à partir de certains arguments.
     *
     * @param pageFetcher Interface fonctionnelle avec la logique de recherche et de tri à faire
     * @param page Numéro de page qui sera pris en compte dans la liste retournée
     * @param size Nombre d'élements qui sera chargé dans la liste retournée
     * @param sort Objet de tri pris en compte dans la liste retournée
     * @return
     */
    protected LIST_DTO getListEntity(PageFetcher<ENTITY> pageFetcher, Integer page, Integer size, Sort sort) {
        // Traitement du cas où on a pas envoyé le numéro de page à afficher ou bien si ce numéro est néatif,
        // on lui affecte une valeur par défaut.
        if (page == null || page < 0) {
            page = 0;
        }
        // Traitement du cas où on a pas envoyé le nombre d'éléments à afficher par page n'est pas envoyé ou
        // si ce nombre est néatif, on lui affecte une valeur par défaut.
        if (size == null || size < 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ENTITY> datas = pageFetcher.getItems(pageable);
        LIST_DTO listDto = (LIST_DTO) new BaseListDto<DTO>();
        listDto.setStatus(HttpStatus.OK.value());
        listDto.setMessage("Liste des " + dto.getClass().getName());
        listDto.setData(
                new BaseListDto.PagedItemList(
                        dto.getListDtoFromListEntity(datas.getContent()),
                        datas.getTotalPages(),
                        datas.getTotalElements()
                )
        );
        return listDto;
    }

    /**
     * Affichage de la liste paginée d'une entité ceci dans un ResponseEntity.
     * @param page Numéro de page que l'on souhaiterait avoir
     * @param size Nombre d'élements à afficher par page
     * @return Objet ResponseEntity avec en corps la liste paginée des entités
     */
    @GetMapping
    public ResponseEntity<LIST_DTO> list(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                         @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        LIST_DTO listDto = null;
        // Cas où on souhaité paginé la liste à retourner suivant une logique
        if (paginated) {
            // Implémentation de la logique de pagination avec une interface fonctionnelle qui est associé à l'entité
            PageFetcher pageFetcher = (PageFetcher<ENTITY>) pageable -> repository.findAll(pageable);
            Sort sort = getSort();
            if (sort == null) {
                sort = Sort.unsorted();
            }
            listDto = (LIST_DTO) getListEntity(pageFetcher, page, size, sort);
        } else {
            Page<ENTITY> datas = repository.findAll(Pageable.unpaged());
            // Dans le cas où on ne souhaiterait pas paginé
            listDto = (LIST_DTO) new BaseListDto<DTO>();
            listDto.setStatus(HttpStatus.OK.value());
            listDto.setMessage("Liste des " + dto.getClass().getName());
            listDto.setData(new BaseListDto.PagedItemList(
                    dto.getListDtoFromListEntity(datas.getContent()),
                    datas.getTotalPages(),
                    datas.getTotalElements()));
        }
        return output(listDto, HttpStatus.OK);
    }

    /**
     * Affiche le détail d'une entité à partir de son id.
     * @param id Identifiant de l'entité dont on souhaite voir les détails
     * @return L'objet ResponseEntity dont le corps est le DTO avec les détails
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DETAIL_DTO>> details(@PathVariable(value = "id") KEY id) {
        ENTITY entity = repository.findById(id).orElse(null);
        ApiResponse<DETAIL_DTO> apiResponse = new ApiResponse<>();
        // Cas où l'entité recherché n'existe pas
        if (entity == null) {
            apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("L'élement d'identifiant " + id + " récherché est inexistant.");
            return output(apiResponse, HttpStatus.NOT_FOUND);
        }
        // Cas où l'entité recherché a été supprimé
        if (entity.getDeletedAt() != null) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setMessage("L'élement d'identifiant " + id + " récherché a été supprimé.");
            return output(apiResponse, HttpStatus.UNAUTHORIZED);
        }
        // Cas où l'entité recherché existe
        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setMessage("Détail de l'entité");
        apiResponse.setData((DETAIL_DTO) detailDto.getDtoFromEntity(entity));
        return output(apiResponse, HttpStatus.OK);
    }

    /**
     * Restaure une entité qui a été supprimée.
     * Restaurer consiste à enlever la date de suppression de l'entité dont on souhaite restaurer.
     * @param id Identifiant de l'entité dont on souhaite restaurer
     * @return L'objet ResponseEntity dont le corps est le DTO restauré
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<DTO>> restore(@PathVariable(value = "id") KEY id) {
        ENTITY entity = repository.findById(id).orElse(null);
        // Cas où l'entité recherché n'existe pas
        if (entity == null) {
            return responseIdNotExist(id);
        }
        ApiResponse<DTO> apiResponse = new ApiResponse<>();
        // Cas où l'entité existe et n'a pas été supprimé, donc impossible de restaurer une entité qui n'a pas été supprimé au préalable
        if (entity.getDeletedAt() == null) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setMessage("L'élement d'identifiant " + id + " ne peut être restauré car il est déjà actif.");
            return output(apiResponse, HttpStatus.UNAUTHORIZED);
        }
        // Cas où l'entité existe et peut être supprimé
        entity.setDeletedAt(null);
        ENTITY entityRestored = repository.save(entity);
        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setMessage("Entité restaurée");
        apiResponse.setData((DTO) dto.getDtoFromEntity(entityRestored));
        return output(apiResponse, HttpStatus.OK);
    }

    /**
     * Supprime une entité donnée.
     * Supprimer consiste à attribuer une date de suppression à l'entité dont on souhaite supprimer.
     * @param id Identifiant de l'entité dont on souhaite supprimer
     * @return L'objet ResponseEntity dont le corps est le DTO supprimé
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<DTO>> delete(@PathVariable(value = "id") KEY id) {
        ENTITY entity = repository.findById(id).orElse(null);
        // Cas où l'entité recherché n'existe pas
        if (entity == null) {
            return responseIdNotExist(id);
        }
        ApiResponse<DTO> apiResponse = new ApiResponse<>();
        // Cas où l'entité recherché a été déjà supprimé
        if (entity.getDeletedAt() != null) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setMessage("L'élement d'identifiant " + id + " récherché a été déjà supprimé.");
            return output(apiResponse, HttpStatus.UNAUTHORIZED);
        }
        // Cas où l'entité existe et peut être supprimé
        entity.setDeletedAt(LocalDateTime.now());
        ENTITY entityDeleted = repository.save(entity);
        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setMessage("Entité supprimée");
        apiResponse.setData((DTO) dto.getDtoFromEntity(entityDeleted));
        return output(apiResponse, HttpStatus.OK);
    }

    @ModelAttribute("login")
    public String getLoginRequest(HttpServletRequest request) {
        return (String) request.getAttribute("login");
    }
}
