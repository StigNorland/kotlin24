package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.RepLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.history.RevisionRepository
import java.util.*
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate


/**
 * @author Stig Norland
 */
class AgentAuditTrailListener{

    @Autowired
    private val applicationContext: ApplicationContext? = null


    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: Agency) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }


    @PostLoad
    private fun afterLoad(entity: Agency) {
        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
        log.debug("{}: {}: {} Already loaded ", "Agency" , entity.id, entity.name)
    }

    private fun <T: AbstractEntity>LoadRevisionEntity(uri: UriId, repository: RevisionRepository<T, UUID, Int>): T {
        return with(uri) {
            if (rev != null)
                repository.findRevision(id,rev!!).map {
                    it.entity.rev = rev
                    it.entity
                    }.get()
            else
                repository.findLastChangeRevision(id).map {
                    it.entity.rev = rev
                    it.entity
                }.get()
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AgentAuditTrailListener::class.java)
    }

}
