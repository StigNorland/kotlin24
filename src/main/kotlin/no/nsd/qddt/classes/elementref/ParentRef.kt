package no.nsd.qddt.classes.elementref

import no.nsd.qddt.classes.exception.StackTraceFilter
import no.nsd.qddt.classes.interfaces.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
class ParentRef<T : IDomainObjectParentRef>//            agency = entity.agency.name
    (entity: T) : IParentRef {

    @Transient
    var entity: T? = null
    override var parentRef: IParentRef? = null
    override lateinit var id: UUID
    override lateinit var name: String
    override lateinit var version: Version

    protected val LOG = LoggerFactory.getLogger(this.javaClass)


    override fun toString(): String {
        return """Ref { id=$id, name='$name', parent=$parentRef}"""
    }

    init {
        try {
            id = entity.id!!
            version = entity.version
            name = entity.name
//            agency = entity.agency.name
            parentRef = entity.parentRef
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
