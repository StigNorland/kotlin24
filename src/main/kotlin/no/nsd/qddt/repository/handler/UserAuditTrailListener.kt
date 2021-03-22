package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.User
import no.nsd.qddt.model.interfaces.RepLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate

class UserAuditTrailListener {
    @Autowired
    private val applicationContext: ApplicationContext? = null

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: User) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: User) {
        log.debug("UNTOUCHED - {} : {} : {}", "User" , entity.username, entity.agency.name)
        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserAuditTrailListener::class.java)
    }
}