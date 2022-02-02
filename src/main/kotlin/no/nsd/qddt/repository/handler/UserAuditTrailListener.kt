package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

class UserAuditTrailListener {
//    @Autowired
//    private val applicationContext: ApplicationContext? = null


    @PrePersist
    @PreUpdate
    private fun preSave(entity: User) {
        log.debug("preSave [{}] {}", "USER", entity.username)
        entity.modified = Timestamp.from(Instant.now())
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: User) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: User) {
        log.debug("UNTOUCHED - {} : {} : {}", "User" , entity.username, entity.agency.name)
//        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserAuditTrailListener::class.java)
    }
}
