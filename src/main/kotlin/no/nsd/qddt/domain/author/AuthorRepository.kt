package no.nsd.qddt.domain.author

import no.nsd.qddt.domain.classes.interfaces.BaseRepository
import no.nsd.qddt.domain.universe.Universe
import no.nsd.qddt.domain.universe.UniverseListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "authors", collectionResourceRel = "author", itemResourceRel = "Author", excerptProjection = AuthorListe::class)
interface AuthorRepository : JpaRepository<Author, UUID> {
    fun findAuthorsByAboutContainingOrNameContainingOrEmailContaining(
        about: String?,
        name: String?,
        email: String?,
        pageable: Pageable?
    ): Page<Author?>? //    @Override
    //    Page<Revision<Integer, Author>> findRevisions(UUID uuid, Pageable pageable);
}
