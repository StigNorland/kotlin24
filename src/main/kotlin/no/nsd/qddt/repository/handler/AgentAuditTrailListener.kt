package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Agency
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate


/**
 * @author Stig Norland
 */
class AgentAuditTrailListener{

//    @Autowired
//    private val applicationContext: ApplicationContext? = null

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: Agency) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: Agency) {
        log.debug("UNTOUCHED - {} : {} : {}", "Agency" , entity.id, entity.name)
//        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AgentAuditTrailListener::class.java)
    }

}
