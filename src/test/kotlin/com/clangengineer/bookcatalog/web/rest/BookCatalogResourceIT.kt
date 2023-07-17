package com.clangengineer.bookcatalog.web.rest

import com.clangengineer.bookcatalog.IntegrationTest
import com.clangengineer.bookcatalog.domain.BookCatalog
import com.clangengineer.bookcatalog.repository.BookCatalogRepository
import com.clangengineer.bookcatalog.service.mapper.BookCatalogMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID
import kotlin.test.assertNotNull

/**
 * Integration tests for the [BookCatalogResource] REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BookCatalogResourceIT {
    @Autowired
    private lateinit var bookCatalogRepository: BookCatalogRepository

    @Autowired
    private lateinit var bookCatalogMapper: BookCatalogMapper

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var bookCatalog: BookCatalog

    @BeforeEach
    fun initTest() {
        bookCatalogRepository.deleteAll().block()
        bookCatalog = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createBookCatalog() {
        val databaseSizeBeforeCreate = bookCatalogRepository.findAll().collectList().block().size
        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isCreated

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeCreate + 1)
        val testBookCatalog = bookCatalogList[bookCatalogList.size - 1]

        assertThat(testBookCatalog.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testBookCatalog.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testBookCatalog.author).isEqualTo(DEFAULT_AUTHOR)
        assertThat(testBookCatalog.bookId).isEqualTo(DEFAULT_BOOK_ID)
        assertThat(testBookCatalog.rentCnt).isEqualTo(DEFAULT_RENT_CNT)
    }

    @Test
    @Throws(Exception::class)
    fun createBookCatalogWithExistingId() {
        // Create the BookCatalog with an existing ID
        bookCatalog.id = "existing_id"
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        val databaseSizeBeforeCreate = bookCatalogRepository.findAll().collectList().block().size
        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Throws(Exception::class)
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = bookCatalogRepository.findAll().collectList().block().size
        // set the field null
        bookCatalog.title = null

        // Create the BookCatalog, which fails.
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeTest)
    }
    @Test
    @Throws(Exception::class)
    fun checkAuthorIsRequired() {
        val databaseSizeBeforeTest = bookCatalogRepository.findAll().collectList().block().size
        // set the field null
        bookCatalog.author = null

        // Create the BookCatalog, which fails.
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeTest)
    }

    @Test

    fun getAllBookCatalogs() {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog).block()

        // Get all the bookCatalogList
        webTestClient.get().uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(bookCatalog.id))
            .jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR))
            .jsonPath("$.[*].bookId").value(hasItem(DEFAULT_BOOK_ID?.toInt()))
            .jsonPath("$.[*].rentCnt").value(hasItem(DEFAULT_RENT_CNT))
    }

    @Test

    fun getBookCatalog() {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog).block()

        val id = bookCatalog.id
        assertNotNull(id)

        // Get the bookCatalog
        webTestClient.get().uri(ENTITY_API_URL_ID, bookCatalog.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(bookCatalog.id))
            .jsonPath("$.title").value(`is`(DEFAULT_TITLE))
            .jsonPath("$.description").value(`is`(DEFAULT_DESCRIPTION))
            .jsonPath("$.author").value(`is`(DEFAULT_AUTHOR))
            .jsonPath("$.bookId").value(`is`(DEFAULT_BOOK_ID?.toInt()))
            .jsonPath("$.rentCnt").value(`is`(DEFAULT_RENT_CNT))
    }
    @Test

    fun getNonExistingBookCatalog() {
        // Get the bookCatalog
        webTestClient.get().uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun putExistingBookCatalog() {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog).block()

        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size

        // Update the bookCatalog
        val updatedBookCatalog = bookCatalogRepository.findById(bookCatalog.id).block()
        updatedBookCatalog.title = UPDATED_TITLE
        updatedBookCatalog.description = UPDATED_DESCRIPTION
        updatedBookCatalog.author = UPDATED_AUTHOR
        updatedBookCatalog.bookId = UPDATED_BOOK_ID
        updatedBookCatalog.rentCnt = UPDATED_RENT_CNT
        val bookCatalogDTO = bookCatalogMapper.toDto(updatedBookCatalog)

        webTestClient.put().uri(ENTITY_API_URL_ID, bookCatalogDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isOk

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
        val testBookCatalog = bookCatalogList[bookCatalogList.size - 1]
        assertThat(testBookCatalog.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBookCatalog.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testBookCatalog.author).isEqualTo(UPDATED_AUTHOR)
        assertThat(testBookCatalog.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testBookCatalog.rentCnt).isEqualTo(UPDATED_RENT_CNT)
    }

    @Test
    fun putNonExistingBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, bookCatalogDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithIdMismatchBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithMissingIdPathParamBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun partialUpdateBookCatalogWithPatch() {
        bookCatalogRepository.save(bookCatalog).block()

        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size

// Update the bookCatalog using partial update
        val partialUpdatedBookCatalog = BookCatalog().apply {
            id = bookCatalog.id

            author = UPDATED_AUTHOR
            bookId = UPDATED_BOOK_ID
            rentCnt = UPDATED_RENT_CNT
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBookCatalog.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedBookCatalog))
            .exchange()
            .expectStatus()
            .isOk

// Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
        val testBookCatalog = bookCatalogList.last()
        assertThat(testBookCatalog.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testBookCatalog.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testBookCatalog.author).isEqualTo(UPDATED_AUTHOR)
        assertThat(testBookCatalog.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testBookCatalog.rentCnt).isEqualTo(UPDATED_RENT_CNT)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdateBookCatalogWithPatch() {
        bookCatalogRepository.save(bookCatalog).block()

        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size

// Update the bookCatalog using partial update
        val partialUpdatedBookCatalog = BookCatalog().apply {
            id = bookCatalog.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
            author = UPDATED_AUTHOR
            bookId = UPDATED_BOOK_ID
            rentCnt = UPDATED_RENT_CNT
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBookCatalog.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedBookCatalog))
            .exchange()
            .expectStatus()
            .isOk

// Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
        val testBookCatalog = bookCatalogList.last()
        assertThat(testBookCatalog.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBookCatalog.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testBookCatalog.author).isEqualTo(UPDATED_AUTHOR)
        assertThat(testBookCatalog.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testBookCatalog.rentCnt).isEqualTo(UPDATED_RENT_CNT)
    }

    @Throws(Exception::class)
    fun patchNonExistingBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, bookCatalogDTO.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithIdMismatchBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamBookCatalog() {
        val databaseSizeBeforeUpdate = bookCatalogRepository.findAll().collectList().block().size
        bookCatalog.id = UUID.randomUUID().toString()

        // Create the BookCatalog
        val bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(bookCatalogDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the BookCatalog in the database
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deleteBookCatalog() {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog).block()
        val databaseSizeBeforeDelete = bookCatalogRepository.findAll().collectList().block().size
        // Delete the bookCatalog
        webTestClient.delete().uri(ENTITY_API_URL_ID, bookCatalog.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val bookCatalogList = bookCatalogRepository.findAll().collectList().block()
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_AUTHOR = "AAAAAAAAAA"
        private const val UPDATED_AUTHOR = "BBBBBBBBBB"

        private const val DEFAULT_BOOK_ID: Long = 1L
        private const val UPDATED_BOOK_ID: Long = 2L

        private const val DEFAULT_RENT_CNT: Int = 1
        private const val UPDATED_RENT_CNT: Int = 2

        private val ENTITY_API_URL: String = "/api/book-catalogs"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): BookCatalog {
            val bookCatalog = BookCatalog(
                title = DEFAULT_TITLE,

                description = DEFAULT_DESCRIPTION,

                author = DEFAULT_AUTHOR,

                bookId = DEFAULT_BOOK_ID,

                rentCnt = DEFAULT_RENT_CNT

            )

            return bookCatalog
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): BookCatalog {
            val bookCatalog = BookCatalog(
                title = UPDATED_TITLE,

                description = UPDATED_DESCRIPTION,

                author = UPDATED_AUTHOR,

                bookId = UPDATED_BOOK_ID,

                rentCnt = UPDATED_RENT_CNT

            )

            return bookCatalog
        }
    }
}
