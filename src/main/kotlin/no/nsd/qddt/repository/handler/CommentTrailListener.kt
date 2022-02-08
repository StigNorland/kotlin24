package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.PrePersist
import javax.persistence.PreUpdate


/**
 * @author Stig Norland
 */
class CommentTrailListener{
//
//    @Autowired
//    private val applicationContext: ApplicationContext? = null

//    @PreRemove
//    private fun beforeAnyUpdate(entity: Comment) {
//        log.debug("About to delete entity: {}" , entity.id)
//    }

    @PrePersist
    @PreUpdate
    private fun onInsert(entity: Comment) {
        log.debug("PrePersist [COMMENT] {}" , entity.id)
        val user = SecurityContextHolder.getContext().authentication.principal as User
        entity.modifiedBy = user
//        entity.modified = Timestamp.from(Instant.now())

    }


//    @PostPersist
//    @PostUpdate
//    @PostRemove
//    private fun afterAnyUpdate(entity: Comment) {
//        log.debug("AFTER_ANY_UPDATE {} {} : {}", "[COMMENT]".padEnd(15) , entity.id, entity.comment?.take(20))
//    }
//
//    @PostLoad
//    private fun afterLoad(entity: Comment) {
//        log.debug("Afterload {} {} : {}", "[COMMENT]".padEnd(15) , entity.id, entity.comment?.take(20))
//    }



    companion object {
        private val log: Logger = LoggerFactory.getLogger(CommentTrailListener::class.java)
    }

}
