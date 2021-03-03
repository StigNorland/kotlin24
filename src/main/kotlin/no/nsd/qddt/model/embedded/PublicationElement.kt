package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IDomainObject
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

    @Column(name = "element_revision", insertable =false, updatable=false )
    override var elementRevision: Int? = null

    @Column(name = "element_name", length = 500)
    override var name: String? = null

    @AttributeOverrides(
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision")),
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
                elementRevision = it.version.rev?:0
                version = it.version
                if (version.rev == 0)
                    version.rev = elementRevision?:0
            }
            if (value == null) {
                name = null
                elementRevision = null
            }
        }

    public override fun clone(): PublicationElement {
        return PublicationElement().apply { 
            this.element = element
         }
    }

}
