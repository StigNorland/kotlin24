package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Author
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate

class AuthorAuditTrailListener {
    @Autowired
    private val applicationContext: ApplicationContext? = null

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: Author) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: Author) {
        log.debug("UNTOUCHED - {} : {} : {}", "Agency" , entity.id, entity.name)
//        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuthorAuditTrailListener::class.java)
    }
}
