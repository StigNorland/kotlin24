package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.classes.AbstractEntityAudit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.*

/**
 * @author Stig Norland
 */
class EntityAuditTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    private fun beforeAnyUpdate(entity: AbstractEntityAudit) {
        if (!entity.version.isModified) {
            log.info("About to add a entity")
        } else {
            log.info("About to update/delete entity: {}" , entity.id)
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: AbstractEntityAudit) {
        log.info("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: AbstractEntityAudit) {
        log.info("Entiry loaded from database: {}" , entity.id)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)
    }
}
