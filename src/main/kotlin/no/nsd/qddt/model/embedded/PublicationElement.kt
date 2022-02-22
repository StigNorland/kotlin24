package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.classes.UriId
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

    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision"))
    )
    @Embedded
    override lateinit var uri: UriId

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

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
            field = value?.also { item ->
                uri = UriId().also {
                    it.id = item.id!!
                    it.rev = item.version.rev
                }
                name = item.name
                version = item.version
            }
        }

    public override fun clone(): PublicationElement {
        return PublicationElement().apply { 
            this.element = element
         }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PublicationElement) return false

        if (uri != other.uri) return false
        if (elementKind != other.elementKind) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + elementKind.hashCode()
        return result
    }

    override fun toString(): String {
        return "PublicationElement(uri=$uri, elementKind=$elementKind)"
    }

}
