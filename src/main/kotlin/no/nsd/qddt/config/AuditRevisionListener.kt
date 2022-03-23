package no.nsd.qddt.config

import org.hibernate.envers.EntityTrackingRevisionListener
import org.hibernate.envers.RevisionType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


class AuditRevisionListener : EntityTrackingRevisionListener {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PersistenceContext
    protected val entityManager: EntityManager? = null


    override fun newRevision(revisionEntity: Any) {
        with (revisionEntity as RevisionEntityImpl) {
            modifiedBy =  SecurityContextHolder.getContext().authentication.principal as no.nsd.qddt.model.User
            modified = Timestamp.from(Instant.now())
            logger.debug("newRevision {}", id)
        }
    }

    override fun entityChanged(
        entityClass: Class<*>?,
        entityName: String?,
        entityId: Serializable?,
        revisionType: RevisionType?,
        revisionEntity: Any?
    ) {
//        val reader = AuditReaderFactory.get(entityManager)
        with (revisionEntity as RevisionEntityImpl) {

//            reader.findRevision(entityClass, revisionEntity.id).runCatching {
//                with(this as IBasedOn) {
                    logger.debug("{} {} {} {}", entityName, entityId, revisionEntity.id, "changeComment")
//                }
//            }
        }
    }
}
