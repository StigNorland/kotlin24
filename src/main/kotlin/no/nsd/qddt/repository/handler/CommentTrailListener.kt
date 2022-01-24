package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.core.context.SecurityContextHolder
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.persistence.*


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
        entity.modified = Timestamp.from(Instant.now())

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
