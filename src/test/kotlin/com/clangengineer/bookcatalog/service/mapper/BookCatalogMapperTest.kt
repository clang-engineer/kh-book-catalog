package com.clangengineer.bookcatalog.service.mapper

import org.junit.jupiter.api.BeforeEach

class BookCatalogMapperTest {

    private lateinit var bookCatalogMapper: BookCatalogMapper

    @BeforeEach
    fun setUp() {
        bookCatalogMapper = BookCatalogMapperImpl()
    }
}
