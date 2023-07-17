package com.clangengineer.bookcatalog.service
import com.clangengineer.bookcatalog.service.dto.BookCatalogDTO
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Interface for managing [com.clangengineer.bookcatalog.domain.BookCatalog].
 */
interface BookCatalogService {

    /**
     * Save a bookCatalog.
     *
     * @param bookCatalogDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO>

    /**
     * Updates a bookCatalog.
     *
     * @param bookCatalogDTO the entity to update.
     * @return the persisted entity.
     */
    fun update(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO>

    /**
     * Partially updates a bookCatalog.
     *
     * @param bookCatalogDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO>

    /**
     * Get all the bookCatalogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<BookCatalogDTO>

    /**
     * Returns the number of bookCatalogs available.
     * @return the number of entities in the database.
     */
    fun countAll(): Mono<Long>
    /**
     * Get the "id" bookCatalog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<BookCatalogDTO>

    /**
     * Delete the "id" bookCatalog.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    fun delete(id: String): Mono<Void>
}
