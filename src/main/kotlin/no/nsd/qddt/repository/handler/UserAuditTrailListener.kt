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
    private fun prePersist(entity: User) {
        log.debug("prePersist [{}] {}", "USER", entity.username)
        entity.modified = Timestamp.from(Instant.now())
    }

    @PreUpdate
    private fun preUpdate(entity: User) {
        log.debug("preUpdate [{}] {}", "USER", entity.username)
        entity.modified = Timestamp.from(Instant.now())
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserAuditTrailListener::class.java)
    }
}
