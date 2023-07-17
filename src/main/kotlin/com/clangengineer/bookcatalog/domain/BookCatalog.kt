package com.clangengineer.bookcatalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import javax.validation.constraints.*

/**
 * A BookCatalog.
 */
@Document(collection = "book_catalog")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class BookCatalog(

    @Id
    var id: String? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 20)
    @Field("title")
    var title: String? = null,
    @Field("description")
    var description: String? = null,

    @get: NotNull(message = "must not be null")
    @Field("author")
    var author: String? = null,
    @Field("book_id")
    var bookId: Long? = null,
    @Field("rent_cnt")
    var rentCnt: Int? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookCatalog) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "BookCatalog{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", author='" + author + "'" +
            ", bookId=" + bookId +
            ", rentCnt=" + rentCnt +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
