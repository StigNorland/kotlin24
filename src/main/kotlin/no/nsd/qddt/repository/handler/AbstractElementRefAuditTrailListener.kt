package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.classes.ElementLoader
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.repository.ElementRepositoryLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.PostLoad
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

/**
 * @author Stig Norland
 */
abstract class AbstractElementRefAuditTrailListener<T : IWebMenuPreview> {

    @Autowired
    lateinit var elementRepositoryLoader: ElementRepositoryLoader


    @PrePersist
    @PreUpdate
    private fun beforeAnyUpdate(entity: IElementRef<T>) {
    }

    @PostLoad
    private fun afterLoad(entity: IElementRef<T>) {
        log.info("After load: {} {}" , entity.name, entity.elementKind)
        val repository =  elementRepositoryLoader.getRepository(entity.elementKind)
        val loader = ElementLoader<T>(repository)
        loader.fill(entity)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
