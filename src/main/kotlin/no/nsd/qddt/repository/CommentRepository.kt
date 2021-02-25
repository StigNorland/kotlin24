package no.nsd.qddt.repository

import no.nsd.qddt.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "comment",  itemResourceRel = "Comment")
interface CommentRepository: JpaRepository<Comment, UUID>
