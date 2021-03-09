package no.nsd.qddt.config

import org.hibernate.envers.RevisionListener
import org.springframework.security.core.context.SecurityContextHolder


class AuditRevisionListener : RevisionListener {
    override fun newRevision(revisionEntity: Any) {
//        System.err.println("setModifiedBy");
        with (revisionEntity as RevisionEntityImpl) {
            modifiedBy =  SecurityContextHolder.getContext().authentication.details as no.nsd.qddt.model.User
        }
    }
}