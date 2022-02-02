package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IDomainObject
import no.nsd.qddt.model.interfaces.IElementRef
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Embeddable
class PublicationElement : IElementRef<IDomainObject> , Serializable {

    override var elementId: UUID?=null

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Column(name = "element_revision", nullable = false)
    override var elementRevision: Int? = null

    @Column(name = "element_name", length = 500)
    override var name: String? = null

    @AttributeOverrides(
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision", insertable = false, updatable = false)),
        AttributeOverride(name = "versionLabel",column = Column(name = "element_version_label"))
    )
    @Transient
    override var version: Version = Version()


    @Transient
    @JsonSerialize
    override var element: IDomainObject? = null
        set(value) {
            field = value?.also {
                elementId = it.id
                name = it.name
                version = it.version
                if (version.rev == 0)
                    version.rev = elementRevision?:0
            }
        }

    public override fun clone(): PublicationElement {
        return PublicationElement().apply { 
            this.element = element
         }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicationElement

        if (elementId != other.elementId) return false
        if (elementKind != other.elementKind) return false
        if (elementRevision != other.elementRevision) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elementId?.hashCode() ?: 0
        result = 31 * result + elementKind.hashCode()
        result = 31 * result + (elementRevision ?: 0)
        return result
    }

    override fun toString(): String {
        return "PublicationElement(elementId=$elementId, elementKind=$elementKind, elementRevision=$elementRevision?:0)"
    }

}
