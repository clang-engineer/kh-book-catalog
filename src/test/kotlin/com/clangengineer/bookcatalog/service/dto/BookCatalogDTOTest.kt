package com.clangengineer.bookcatalog.service.dto

import com.clangengineer.bookcatalog.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookCatalogDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(BookCatalogDTO::class)
        val bookCatalogDTO1 = BookCatalogDTO()
        bookCatalogDTO1.id = "id1"
        val bookCatalogDTO2 = BookCatalogDTO()
        assertThat(bookCatalogDTO1).isNotEqualTo(bookCatalogDTO2)
        bookCatalogDTO2.id = bookCatalogDTO1.id
        assertThat(bookCatalogDTO1).isEqualTo(bookCatalogDTO2)
        bookCatalogDTO2.id = "id2"
        assertThat(bookCatalogDTO1).isNotEqualTo(bookCatalogDTO2)
        bookCatalogDTO1.id = null
        assertThat(bookCatalogDTO1).isNotEqualTo(bookCatalogDTO2)
    }
}
