package no.nsd.qddt.model.classes

import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.interfaces.IDomainObjectParentRef
import no.nsd.qddt.model.interfaces.IParentRef
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.Column
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
class ParentRef<T : IDomainObjectParentRef>//            agency = entity.agency.name
    (entity: T) : IParentRef {

    @Transient
    var entity: T? = null
    override var parentRef: IParentRef? = null
    @Column(updatable = false, nullable = false)
    override var id: UUID?=null
    override lateinit var name: String
    override var version: Version = Version()

    private val logger = LoggerFactory.getLogger(this.javaClass)


    override fun toString(): String {
        return """Ref { id=$id, name='$name', parent=$parentRef}"""
    }

    init {
        try {
            id = entity.id
            version = entity.version
            name = entity.name
//            agency = entity.agency.name
            parentRef = entity.parentRef
            this.entity = entity
        } catch (npe: NullPointerException) {
            logger.error(StackTraceFilter.filter(npe.stackTrace).stream().map { a: StackTraceElement? -> a.toString() }
                .findFirst().get())
        } catch (ex: Exception) {
            logger.error(ex.message)
            StackTraceFilter.filter(ex.stackTrace).stream().map { a: StackTraceElement? -> a.toString() }
                .forEach { msg: String? -> logger.info(msg) }
        }
    }

}
