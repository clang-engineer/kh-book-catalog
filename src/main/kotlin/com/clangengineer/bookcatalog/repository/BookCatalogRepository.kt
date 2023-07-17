package com.clangengineer.bookcatalog.repository

import com.clangengineer.bookcatalog.domain.BookCatalog
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
* Spring Data MongoDB reactive repository for the BookCatalog entity.
*/
@SuppressWarnings("unused")
@Repository
interface BookCatalogRepository : ReactiveMongoRepository<BookCatalog, String> {

    fun findAllBy(pageable: Pageable?): Flux<BookCatalog>
}
