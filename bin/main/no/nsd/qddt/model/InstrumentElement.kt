package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.embedded.Parameter
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IDomainObject
import no.nsd.qddt.model.interfaces.IElementRef
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */

@Audited
@Entity
@Table(name = "INSTRUMENT_NODE")
@Cacheable
data class InstrumentElement(
    @Id @GeneratedValue var id: UUID? = null
) : IElementRef<IDomainObject> , Serializable {

    @Column(insertable = false, updatable = false)
    var parentIdx: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    var parent: InstrumentElement? = null

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE], orphanRemoval = true , targetEntity = InstrumentElement::class)
    var children: MutableList<InstrumentElement> = mutableListOf()

    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision"))
    )
    @Embedded
    override var uri: UriId = UriId()

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Column(name = "element_name", length = 1500)
    override var name: String? = null

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision", insertable = false, updatable = false)),
        AttributeOverride(name = "versionLabel",column = Column(name = "element_version_label"))
    )
    override var version: Version = Version()

    @OrderColumn(name = "node_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "INSTRUMENT_PARAMETER",
        joinColumns = [JoinColumn(name = "node_id", referencedColumnName = "id")]
    )
    var parameters: MutableList<Parameter> = mutableListOf()

    @Transient
    @JsonSerialize(contentAs = ControlConstruct::class)
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

    public override fun clone(): InstrumentElement {
        return InstrumentElement().apply {
            this.element = element
         }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InstrumentElement) return false

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
        return "Element(uri=$uri, elementKind=$elementKind)"
    }

}
