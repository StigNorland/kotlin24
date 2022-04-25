package no.nsd.qddt.config

import org.hibernate.envers.EntityTrackingRevisionListener
import org.hibernate.envers.RevisionType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
class AuditRevisionListener : EntityTrackingRevisionListener {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PersistenceContext
    lateinit var entityManager: EntityManager


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

        with (revisionEntity as RevisionEntityImpl) {
//            entityManager!!.criteriaBuilder.let { cb ->
//                cb.createQuery(entityClass).let { cq ->
//                    cq.select(cq.from(entityClass).get("changeComment"))
//                    cq.from(entityClass).let { entity ->
//                        cq.where(cb.equal(entity.get<UUID>("id"),entityId))
//                    }
//                    entityManager.createQuery(cq).singleResult.let {
//                        logger.debug(it.toString())
//                    }
//                }
//            }
//            this@AuditRevisionListener.entityManager
//            var result =entityManager!!.find(entityClass,entityId)
//
//            changeComment = (result as AbstractEntityAudit).changeComment

//            reader.findRevision(entityClass, revisionEntity.id).runCatching {
//                with(this as IBasedOn) {
                    logger.debug("{} {} {} {}", entityName, entityId, revisionEntity.id, "changeComment")
//                }
//            }
        }
    }
}
