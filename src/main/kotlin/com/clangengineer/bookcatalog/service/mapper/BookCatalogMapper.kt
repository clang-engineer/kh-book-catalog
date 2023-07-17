package com.clangengineer.bookcatalog.service.mapper

import com.clangengineer.bookcatalog.domain.BookCatalog
import com.clangengineer.bookcatalog.service.dto.BookCatalogDTO
import org.mapstruct.*

/**
 * Mapper for the entity [BookCatalog] and its DTO [BookCatalogDTO].
 */
@Mapper(componentModel = "spring")
interface BookCatalogMapper :
    EntityMapper<BookCatalogDTO, BookCatalog>
