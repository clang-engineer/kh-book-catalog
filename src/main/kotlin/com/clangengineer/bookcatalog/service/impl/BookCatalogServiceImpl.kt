package com.clangengineer.bookcatalog.service.impl

import com.clangengineer.bookcatalog.domain.BookCatalog
import com.clangengineer.bookcatalog.repository.BookCatalogRepository
import com.clangengineer.bookcatalog.service.BookCatalogService
import com.clangengineer.bookcatalog.service.dto.BookCatalogDTO
import com.clangengineer.bookcatalog.service.mapper.BookCatalogMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [BookCatalog].
 */
@Service
class BookCatalogServiceImpl(
    private val bookCatalogRepository: BookCatalogRepository,
    private val bookCatalogMapper: BookCatalogMapper,
) : BookCatalogService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO> {
        log.debug("Request to save BookCatalog : $bookCatalogDTO")
        return bookCatalogRepository.save(bookCatalogMapper.toEntity(bookCatalogDTO))
            .map(bookCatalogMapper::toDto)
    }

    override fun update(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO> {
        log.debug("Request to update BookCatalog : {}", bookCatalogDTO)
        return bookCatalogRepository.save(bookCatalogMapper.toEntity(bookCatalogDTO))
            .map(bookCatalogMapper::toDto)
    }

    override fun partialUpdate(bookCatalogDTO: BookCatalogDTO): Mono<BookCatalogDTO> {
        log.debug("Request to partially update BookCatalog : {}", bookCatalogDTO)

        return bookCatalogRepository.findById(bookCatalogDTO.id)
            .map {
                bookCatalogMapper.partialUpdate(it, bookCatalogDTO)
                it
            }
            .flatMap { bookCatalogRepository.save(it) }
            .map { bookCatalogMapper.toDto(it) }
    }

    override fun findAll(pageable: Pageable): Flux<BookCatalogDTO> {
        log.debug("Request to get all BookCatalogs")
        return bookCatalogRepository.findAllBy(pageable)
            .map(bookCatalogMapper::toDto)
    }

    override fun countAll() = bookCatalogRepository.count()

    override fun findOne(id: String): Mono<BookCatalogDTO> {
        log.debug("Request to get BookCatalog : $id")
        return bookCatalogRepository.findById(id)
            .map(bookCatalogMapper::toDto)
    }

    override fun delete(id: String): Mono<Void> {
        log.debug("Request to delete BookCatalog : $id")
        return bookCatalogRepository.deleteById(id)
    }
}
