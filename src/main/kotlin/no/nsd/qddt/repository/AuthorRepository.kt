package no.nsd.qddt.repository

import no.nsd.qddt.model.Author
import no.nsd.qddt.model.Category
import no.nsd.qddt.repository.projection.AuthorListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "author",  itemResourceRel = "Author", excerptProjection = AuthorListe::class)
interface AuthorRepository : JpaRepository<Author,UUID> {
    @Query(
        value = "SELECT ca.* FROM author ca WHERE " +
                "( ca.name  ILIKE searchStr(:name) " +
                "OR ca.email ILIKE searchStr(:email))" ,
        countQuery = "SELECT count(ca.*) FROM author ca WHERE " +
                "( ca.name  ILIKE searchStr(:name) " +
                "OR ca.email ILIKE searchStr(:email))" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("email") email: String?,
        @Param("name") name: String?,
        pageable: Pageable?
    ): Page<Author?>?
}
