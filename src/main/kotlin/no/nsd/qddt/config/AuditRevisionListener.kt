package no.nsd.qddt.config

import org.hibernate.envers.RevisionListener
import org.springframework.security.core.context.SecurityContextHolder
import java.sql.Timestamp
import java.time.Instant


class AuditRevisionListener : RevisionListener {
    override fun newRevision(revisionEntity: Any) {
        with (revisionEntity as RevisionEntityImpl) {
            modifiedBy =  SecurityContextHolder.getContext().authentication.principal as no.nsd.qddt.model.User
            modified = Timestamp.from(Instant.now())
        }
    }
}