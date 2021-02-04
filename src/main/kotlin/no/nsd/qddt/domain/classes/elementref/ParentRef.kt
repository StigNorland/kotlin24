package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.AbstractEntityAudit.getAgency
import no.nsd.qddt.domain.AbstractEntityAudit.getVersion
import no.nsd.qddt.domain.classes.exception.StackTraceFilter
import no.nsd.qddt.domain.classes.interfaces.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
class ParentRef<T : IDomainObjectParentRef?>(entity: T?) : IParentRef {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
    override var id: UUID? = null
    override var version: Version? = null
    override var name: String? = null
    var agency: String? = null
    override var parentRef: IParentRef? = null

    @Transient
    var entity: T? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val parentRef = o as ParentRef<*>
        if (if (id != null) id != parentRef.id else parentRef.id != null) return false
        if (if (version != null) version != parentRef.version else parentRef.version != null) return false
        if (if (agency != null) agency != parentRef.agency else parentRef.agency != null) return false
        return if (name != null) name == parentRef.name else parentRef.name == null
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (version != null) version.hashCode() else 0
        result = 31 * result + if (agency != null) agency.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return """Ref {
   id=$id,
   name='$name',
   parent=$parentRef}
"""
    }

    init {
        if (entity == null) return
        try {
            id = entity.getId()
            version = entity.getVersion()
            name = entity.getName()
            agency = entity.getAgency().name
            parentRef = entity.getParentRef()
            this.entity = entity
        } catch (npe: NullPointerException) {
            LOG.error(StackTraceFilter.filter(npe.stackTrace).stream().map { a: StackTraceElement? -> a.toString() }
                .findFirst().get())
        } catch (ex: Exception) {
            LOG.error(ex.message)
            StackTraceFilter.filter(ex.stackTrace).stream().map { a: StackTraceElement? -> a.toString() }
                .forEach { msg: String? -> LOG.info(msg) }
        }
    }
}
