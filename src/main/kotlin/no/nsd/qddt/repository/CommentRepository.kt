package no.nsd.qddt.repository

import no.nsd.qddt.model.Comment
import no.nsd.qddt.repository.projection.CommentListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "comment",  itemResourceRel = "Comment", excerptProjection = CommentListe::class)
interface CommentRepository: JpaRepository<Comment, UUID> {

    @RestResource(rel = "findByOwner", path = "byOwner")
    fun findByOwnerId( ownerId: UUID,  pageable: Pageable): Page<Comment>
}
