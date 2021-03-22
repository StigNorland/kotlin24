package no.nsd.qddt.repository

import no.nsd.qddt.model.Author
import no.nsd.qddt.repository.projection.AuthorListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "author",  itemResourceRel = "Author", excerptProjection = AuthorListe::class)
interface AuthorRepository : JpaRepository<Author,UUID> {
    fun findAuthorsByAboutContainingOrNameContainingOrEmailContaining(
        about: String?,
        name: String?,
        email: String?,
        pageable: Pageable?
    ): Page<Author>? //    @Override
    //    Page<Revision<Integer, Author>> findRevisions(UUID uuid, Pageable pageable);
}
