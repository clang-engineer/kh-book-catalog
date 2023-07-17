package com.clangengineer.bookcatalog.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [com.clangengineer.bookcatalog.domain.BookCatalog] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class BookCatalogDTO(

    var id: String? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 20)
    var title: String? = null,

    var description: String? = null,

    @get: NotNull(message = "must not be null")
    var author: String? = null,

    var bookId: Long? = null,

    var rentCnt: Int? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookCatalogDTO) return false
        val bookCatalogDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, bookCatalogDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
