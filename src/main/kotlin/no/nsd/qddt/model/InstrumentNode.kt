package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Parameter
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
@Table(name = "INSTRUMENT_NODE")
@AttributeOverride(name = "name", column = Column(name = "element_name", length = 1500))
data class InstrumentNode<T : ControlConstruct>(
        @Id @GeneratedValue var id: UUID? = null
    ): AbstractElementRef<T>() {

    override var uri: UriId
        get() = super.uri
        set(value) {
            if (value == null) {
                super.uri = UriId()
            } else{
                super.uri = value
            }
        }

    @Column(insertable = false, updatable = false)
    var parentIdx: Int? = 0

    @Column( nullable = false,  insertable = false, updatable = false)
    var parentId: UUID? = null

    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY, targetEntity = InstrumentNode::class)
    @JoinColumn(name="parentId")
    var parent: InstrumentNode<T>? = null

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE], targetEntity = InstrumentNode::class)
    var children: MutableList<InstrumentNode<T>> = mutableListOf()

    @OrderColumn(name = "node_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "INSTRUMENT_PARAMETER",
        joinColumns = [JoinColumn(name = "node_id", referencedColumnName = "id")]
    )
    var parameters: MutableList<Parameter> = mutableListOf()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InstrumentNode<*>) return false

        if (id != other.id) return false
        if (parentIdx != other.parentIdx) return false
//        if (parentId != other.parentId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (parentIdx ?: 0)
        return result
    }

    override fun toString(): String {
        return "InstrumentNode(id=$id, parentIdx=$parentIdx)"
    }

}
