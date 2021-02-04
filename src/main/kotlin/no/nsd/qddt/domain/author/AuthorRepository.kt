package no.nsd.qddt.domain.author

import no.nsd.qddt.domain.classes.interfaces.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface AuthorRepository : BaseRepository<Author?, UUID?> {
    fun findAuthorsByAboutContainingOrNameContainingOrEmailContaining(
        about: String?,
        name: String?,
        email: String?,
        pageable: Pageable?
    ): Page<Author?>? //    @Override
    //    Page<Revision<Integer, Author>> findRevisions(UUID uuid, Pageable pageable);
}
