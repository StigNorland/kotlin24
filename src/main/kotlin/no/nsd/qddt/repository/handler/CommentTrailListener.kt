package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import javax.persistence.PrePersist
import javax.persistence.PreUpdate


/**
 * @author Stig Norland
 */
class CommentTrailListener{

    @PrePersist
    @PreUpdate
    private fun onInsertUpdate(entity: Comment) {
        log.debug("PrePersist [COMMENT] {}" , entity.id)
        val user = SecurityContextHolder.getContext().authentication.principal as User
        entity.modifiedBy = user
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CommentTrailListener::class.java)
    }

}
