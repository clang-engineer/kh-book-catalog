package com.clangengineer.bookcatalog.web.rest

import com.clangengineer.bookcatalog.repository.BookCatalogRepository
import com.clangengineer.bookcatalog.service.BookCatalogService
import com.clangengineer.bookcatalog.service.dto.BookCatalogDTO
import com.clangengineer.bookcatalog.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.reactive.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "bookCatalogBookCatalog"
/**
 * REST controller for managing [com.clangengineer.bookcatalog.domain.BookCatalog].
 */
@RestController
@RequestMapping("/api")
class BookCatalogResource(
    private val bookCatalogService: BookCatalogService,
    private val bookCatalogRepository: BookCatalogRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "bookCatalogBookCatalog"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /book-catalogs` : Create a new bookCatalog.
     *
     * @param bookCatalogDTO the bookCatalogDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new bookCatalogDTO, or with status `400 (Bad Request)` if the bookCatalog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/book-catalogs")
    fun createBookCatalog(@Valid @RequestBody bookCatalogDTO: BookCatalogDTO): Mono<ResponseEntity<BookCatalogDTO>> {
        log.debug("REST request to save BookCatalog : $bookCatalogDTO")
        if (bookCatalogDTO.id != null) {
            throw BadRequestAlertException(
                "A new bookCatalog cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return bookCatalogService.save(bookCatalogDTO)
            .map { result ->
                try {
                    ResponseEntity.created(URI("/api/book-catalogs/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * {@code PUT  /book-catalogs/:id} : Updates an existing bookCatalog.
     *
     * @param id the id of the bookCatalogDTO to save.
     * @param bookCatalogDTO the bookCatalogDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated bookCatalogDTO,
     * or with status `400 (Bad Request)` if the bookCatalogDTO is not valid,
     * or with status `500 (Internal Server Error)` if the bookCatalogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/book-catalogs/{id}")
    fun updateBookCatalog(
        @PathVariable(value = "id", required = false) id: String,
        @Valid @RequestBody bookCatalogDTO: BookCatalogDTO
    ): Mono<ResponseEntity<BookCatalogDTO>> {
        log.debug("REST request to update BookCatalog : {}, {}", id, bookCatalogDTO)
        if (bookCatalogDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, bookCatalogDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return bookCatalogRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            bookCatalogService.update(bookCatalogDTO)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.id))
                        .body(result)
                }
        }
    }

    /**
     * {@code PATCH  /book-catalogs/:id} : Partial updates given fields of an existing bookCatalog, field will ignore if it is null
     *
     * @param id the id of the bookCatalogDTO to save.
     * @param bookCatalogDTO the bookCatalogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCatalogDTO,
     * or with status {@code 400 (Bad Request)} if the bookCatalogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookCatalogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookCatalogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/book-catalogs/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateBookCatalog(
        @PathVariable(value = "id", required = false) id: String,
        @NotNull @RequestBody bookCatalogDTO: BookCatalogDTO
    ): Mono<ResponseEntity<BookCatalogDTO>> {
        log.debug("REST request to partial update BookCatalog partially : {}, {}", id, bookCatalogDTO)
        if (bookCatalogDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, bookCatalogDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return bookCatalogRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            val result = bookCatalogService.partialUpdate(bookCatalogDTO)

            result
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map {
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, it.id))
                        .body(it)
                }
        }
    }

    /**
     * `GET  /book-catalogs` : get all the bookCatalogs.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of bookCatalogs in body.
     */
    @GetMapping("/book-catalogs")
    fun getAllBookCatalogs(@org.springdoc.api.annotations.ParameterObject pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<List<BookCatalogDTO>>> {

        log.debug("REST request to get a page of BookCatalogs")
        return bookCatalogService.countAll()
            .zipWith(bookCatalogService.findAll(pageable).collectList())
            .map {
                ResponseEntity.ok().headers(
                    PaginationUtil.generatePaginationHttpHeaders(
                        UriComponentsBuilder.fromHttpRequest(request),
                        PageImpl(it.t2, pageable, it.t1)
                    )
                ).body(it.t2)
            }
    }

    /**
     * `GET  /book-catalogs/:id` : get the "id" bookCatalog.
     *
     * @param id the id of the bookCatalogDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the bookCatalogDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/book-catalogs/{id}")
    fun getBookCatalog(@PathVariable id: String): Mono<ResponseEntity<BookCatalogDTO>> {
        log.debug("REST request to get BookCatalog : $id")
        val bookCatalogDTO = bookCatalogService.findOne(id)
        return ResponseUtil.wrapOrNotFound(bookCatalogDTO)
    }
    /**
     *  `DELETE  /book-catalogs/:id` : delete the "id" bookCatalog.
     *
     * @param id the id of the bookCatalogDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/book-catalogs/{id}")
    fun deleteBookCatalog(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete BookCatalog : $id")
        return bookCatalogService.delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build<Void>()
                )
            )
    }
}
