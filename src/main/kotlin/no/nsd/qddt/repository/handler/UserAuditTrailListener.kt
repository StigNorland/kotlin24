package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

class UserAuditTrailListener {

    @PrePersist
    @PreUpdate
    private fun preSave(entity: User) {
        log.debug("preSave [{}] {}", "USER", entity.username)
        entity.modified = Timestamp.from(Instant.now())
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserAuditTrailListener::class.java)
    }
}
