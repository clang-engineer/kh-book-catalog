package com.clangengineer.bookcatalog.domain

import com.clangengineer.bookcatalog.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookCatalogTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(BookCatalog::class)
        val bookCatalog1 = BookCatalog()
        bookCatalog1.id = "id1"
        val bookCatalog2 = BookCatalog()
        bookCatalog2.id = bookCatalog1.id
        assertThat(bookCatalog1).isEqualTo(bookCatalog2)
        bookCatalog2.id = "id2"
        assertThat(bookCatalog1).isNotEqualTo(bookCatalog2)
        bookCatalog1.id = null
        assertThat(bookCatalog1).isNotEqualTo(bookCatalog2)
    }
}
